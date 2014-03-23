(ns gl.starter
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.openal AL])
  (:import [org.newdawn.slick Color])
  (:import [org.newdawn.slick.opengl Texture TextureLoader])
  (:import [org.newdawn.slick.openal Audio AudioLoader SoundStore])
  (:import [org.newdawn.slick.util ResourceLoader])
  (:gen-class))

(def application-title "a program")
(def screen-w 800)
(def screen-h 600)


(declare start init-gl init render update load-sounds load-imgs)
(declare wav-effect ogg-stream texture)


(defn clean-up
  []
  (Display/destroy)
  (AL/destroy)
  (shutdown-agents)
  (System/exit 0))

(defn start
  []
  (init-gl screen-w screen-h application-title)
  (init)
  (while (not (Display/isCloseRequested))
    (render)
    (update)
    (Display/update)
    (Display/sync 60))
  (clean-up))

(defn init-gl
  [width height title]
  (try
    (Display/setDisplayMode (DisplayMode. width height))
    (Display/setTitle title)
    (Display/create)
    (Display/setVSyncEnabled true)
    (catch LWJGLException e
      (.printStackTrace e)
      (System/exit 0)))
  (GL11/glShadeModel GL11/GL_SMOOTH)
  (GL11/glDisable GL11/GL_DEPTH_TEST)
  (GL11/glDisable GL11/GL_LIGHTING)
  (GL11/glClearColor 0.3 1.0 0.6 1.0)
  (GL11/glClearDepth 1)
  (GL11/glEnable GL11/GL_BLEND)
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glViewport 0 0 width height)
  (GL11/glOrtho 0 width height 0 0 1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

(defn init
  []
  (load-sounds)
  (load-imgs))

(defn load-sounds
  []
  (try
    (def texture (TextureLoader/getTexture "PNG" (ResourceLoader/getResourceAsStream "img/test.png")))
    (println "Texture loaded: " texture)
    (println ">> Image width: " (.getImageWidth texture))
    (println ">> Image height: " (.getImageHeight texture))
    (println ">> Texture width: " (.getTextureWidth texture))
    (println ">> Texture height: " (.getTextureHeight texture))
    (println ">> Texture ID: " (.getTextureID texture))
    (def texture (TextureLoader/getTexture "PNG" (ResourceLoader/getResourceAsStream "img/creature.png")))
    (println "Texture loaded: " texture)
    (println ">> Image width: " (.getImageWidth texture))
    (println ">> Image height: " (.getImageHeight texture))
    (println ">> Texture width: " (.getTextureWidth texture))
    (println ">> Texture height: " (.getTextureHeight texture))
    (println ">> Texture ID: " (.getTextureID texture))
    (def wav-effect (AudioLoader/getAudio "WAV" (ResourceLoader/getResourceAsStream "./sound/hit.wav")))
    (def ogg-stream (AudioLoader/getStreamingAudio "OGG" (ResourceLoader/getResource "./music/ys3-4.ogg")))
    (catch java.io.IOException e
      (.printStackTrace e))))

(defn load-imgs
  []
  (def texture (TextureLoader/getTexture "PNG" (ResourceLoader/getResourceAsStream "img/test.png"))))

(defn test-start
  []
  (init-gl screen-w screen-h application-title)
  (init)
  (while (not (Display/isCloseRequested))
    (render)
    (update)
    (Display/update)
    (Display/sync 60))
  (Display/destroy))

(defn draw-img
  []
  (GL11/glEnable GL11/GL_TEXTURE_2D)
  (.bind Color/white)
  (.bind texture)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glTexCoord2f 0 0)
  (GL11/glVertex2f 100 100)
  (GL11/glTexCoord2f 1 0)
  (GL11/glVertex2f (+ 100 (.getTextureWidth texture)) 100)
  (GL11/glTexCoord2f 1 1)
  (GL11/glVertex2f (+ 100 (.getTextureWidth texture)) (+ 100 (.getTextureHeight texture)))
  (GL11/glTexCoord2f 0 1)
  (GL11/glVertex2f 100 (+ 100 (.getTextureHeight texture)))
  (GL11/glEnd))

(defn draw-box
  []
  (GL11/glDisable GL11/GL_TEXTURE_2D)
  (GL11/glColor3f 1.0 0.0 1.0)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glVertex2f 100 100)
  (GL11/glVertex2f 100 300)
  (GL11/glVertex2f 300 300)
  (GL11/glVertex2f 300 100)
  (GL11/glEnd))

(defn render
  []
  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
  (draw-box)
  (draw-img))

(defn update
  []
  (while (Keyboard/next)
    (when (Keyboard/getEventKeyState)
      (let [k (Keyboard/getEventKey)]
        (condp = k
          Keyboard/KEY_1 (.playAsSoundEffect wav-effect 1.0 1.0 false)
          Keyboard/KEY_2 (.playAsMusic ogg-stream 1.0 1.0 true)
          nil))))
  (.poll (SoundStore/get) 0))

(defn -main
  [& args]
  (start))

