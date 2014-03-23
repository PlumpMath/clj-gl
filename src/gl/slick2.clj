(ns gl.slick2
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.openal AL])
  (:import [org.newdawn.slick.openal Audio AudioLoader SoundStore])
  (:import [org.newdawn.slick.util ResourceLoader]))

(declare start init-gl init render update)
(declare wav-effect ogg-stream)

(defn clean-up
  []
  (Display/destroy)
  (AL/destroy)
  (shutdown-agents)
  (System/exit 0))

(defn start
  []
  (init-gl 800 600)
  (init)
  (while (not (Display/isCloseRequested))
    (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
    (render)
    (update)
    (Display/update)
    (Display/sync 60))
  (clean-up))

(defn init-gl
  [width height]
  (try
    (Display/setDisplayMode (DisplayMode. width height))
    (Display/create)
    (Display/setVSyncEnabled true)
    (catch LWJGLException e
      (.printStackTrace e)
      (System/exit 0)))
  (GL11/glEnable GL11/GL_TEXTURE_2D)
  (GL11/glShadeModel GL11/GL_SMOOTH)
  (GL11/glDisable GL11/GL_DEPTH_TEST)
  (GL11/glDisable GL11/GL_LIGHTING)
  (GL11/glClearColor 1.0 1.0 1.0 1.0)
  (GL11/glClearDepth 1)
  ;; enable alpha blending
  (GL11/glEnable GL11/GL_BLEND)
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
  (GL11/glViewport 0 0 width height)
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0 width height 0 1 -1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

(defn init
  []
  (try
    (def wav-effect (AudioLoader/getAudio "WAV" (ResourceLoader/getResourceAsStream "./sound/hit.wav")))
    (def ogg-stream (AudioLoader/getStreamingAudio "OGG" (ResourceLoader/getResource "./music/ys3-1.ogg")))
    (catch java.io.IOException e
      (.printStackTrace e))))

(defn test-start
  []
  (init-gl 800 600)
  (init)
  (while (not (Display/isCloseRequested))
    (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
    (render)
    (update)
    (Display/update)
    (Display/sync 60)))

(defn render
  []
  (GL11/glBegin (GL11/GL_LINES))
  (GL11/glVertex2f 100 100)
  (GL11/glVertex2f 100 200)
  (GL11/glVertex2f 200 200)
  (GL11/glVertex2f 200 100)
  (GL11/glEnd))

(defn update
  []
  (while (Keyboard/next)
    (when (Keyboard/getEventKeyState)
      (let [k (Keyboard/getEventKey)]
        (condp = k
          Keyboard/KEY_1 (.playAsSoundEffect wav-effect 1.0 1.0 false)
          Keyboard/KEY_2 (.playAsMusic ogg-stream 1.0 1.0 true)))))
  (.poll (SoundStore/get) 0))


(defn -main
  [& args]
  (start))

