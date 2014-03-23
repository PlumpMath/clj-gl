(ns gl.2d
  (:import [java.nio ByteBuffer ByteOrder IntBuffer FloatBuffer])
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU])
  (:import [org.newdawn.slick Color])
  (:import [org.newdawn.slick.opengl Texture TextureLoader])
  (:import [org.newdawn.slick.util ResourceLoader])
  (:gen-class
    :name gl.2d
    :methods [#^{:static true} [start [] Void]]))

(def application-on? true)
(def window-title "2D Engine with OpenGL")
(def fullscreen? false)
(def display-mode nil)
(def default-screen-w 640)
(def default-screen-h 480)
(def default-screen-bpp 24)
(def screen-w 800)
(def screen-h 600)
(def screen-bpp 24)

(def fps-limit 60)

(declare texture-font-formal
         texture-font-casual
         texture-creature
         texture-floor
         texture-side)

(declare run main-loop switch-mode! switch-light! switch-blend! render create-window
         process-key-event update get-time
         init init-gl load-textures clean-up)

(defn get-time
  []
  (/ (* (Sys/getTime) 1000) (Sys/getTimerResolution)))

(defn -start
  []
  (def application-on? true)
  (run))

(defn run
  []
  (try
    (init)
    (while application-on?
      (main-loop)
      (render)
      (Display/update))
    (clean-up)
    (catch Exception e
      (.printStackTrace e))))

(defn switch-light!
  []
  (def light? (not light?))
  (if light?
    (GL11/glEnable GL11/GL_LIGHTING)
    (GL11/glDisable GL11/GL_LIGHTING)))

(defn switch-blend!
  []
  (def blend? (not blend?))
  (if blend?
    (do
      (GL11/glEnable GL11/GL_BLEND)
      (GL11/glDisable GL11/GL_DEPTH_TEST))
    (do
      (GL11/glDisable GL11/GL_BLEND)
      (GL11/glEnable GL11/GL_DEPTH_TEST))))

(defn process-key-event
  []
  (while (Keyboard/next)
    (let [key-state (Keyboard/getEventKey)]
      (when (Keyboard/getEventKeyState)
        (condp = key-state
          Keyboard/KEY_F1 (switch-mode!)
          Keyboard/KEY_ESCAPE (def application-on? false)
          Keyboard/KEY_L (switch-light!)
          Keyboard/KEY_F (def filter-n (rem (+ 1 filter-n) 2))
          Keyboard/KEY_B (switch-blend!)
          Keyboard/KEY_PRIOR (def z (- z 0.02))
          Keyboard/KEY_NEXT (def z (+ z 0.02))
          Keyboard/KEY_UP (def x-speed (- x-speed 0.01))
          Keyboard/KEY_DOWN (def x-speed (+ x-speed 0.01))
          Keyboard/KEY_LEFT (def y-speed (- y-speed 0.01))
          Keyboard/KEY_RIGHT (def y-speed (+ y-speed 0.01))
          nil)))))

(defn main-loop
  []
  (process-key-event)
  (when (Display/isCloseRequested)
    (def application-on? false))
  (Display/sync fps-limit))

(defn switch-mode!
  []
  (def fullscreen? (not fullscreen?))
  (try
    (Display/setFullscreen fullscreen?)
    (catch Exception e
      (.printStackTrace e))))

(defn render
  []
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (GL11/glLoadIdentity)
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)

  (let [c [1.0 0.5 0.2]]
    (apply #(GL11/glColor3f % %2 %3) c))
  (.bind texture-font-formal)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glTexCoord2f (/ 1 32) (/ 30 512)) (GL11/glVertex2f 0 0)
  (GL11/glTexCoord2f (/ 1 32) (/ 31 512)) (GL11/glVertex2f 0 16)
  (GL11/glTexCoord2f (/ 2 32) (/ 31 512)) (GL11/glVertex2f 16 16)
  (GL11/glTexCoord2f (/ 2 32) (/ 30 512)) (GL11/glVertex2f 16 0)
  (GL11/glEnd)
  
  (.bind texture-font-casual)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glTexCoord2f (/ 1 32) (/ 30 512)) (GL11/glVertex2f 100 0)
  (GL11/glTexCoord2f (/ 1 32) (/ 31 512)) (GL11/glVertex2f 100 16)
  (GL11/glTexCoord2f (/ 2 32) (/ 31 512)) (GL11/glVertex2f 116 16)
  (GL11/glTexCoord2f (/ 2 32) (/ 30 512)) (GL11/glVertex2f 116 0)
  (GL11/glEnd)
  
  (GL11/glColor3f 1.0 1.0 1.0)
  (.bind texture-creature)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glTexCoord2f (/ 15 16) (/ 0 32)) (GL11/glVertex2f 200 0)
  (GL11/glTexCoord2f (/ 15 16) (/ 1 32)) (GL11/glVertex2f 200 32)
  (GL11/glTexCoord2f (/ 16 16) (/ 1 32)) (GL11/glVertex2f 232 32)
  (GL11/glTexCoord2f (/ 16 16) (/ 0 32)) (GL11/glVertex2f 232 0)
  (GL11/glEnd)
  
  (.bind texture-floor)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glTexCoord2f (/ 120 512) (/ 44 512)) (GL11/glVertex2f 300 0)
  (GL11/glTexCoord2f (/ 120 512) (/ 66 512)) (GL11/glVertex2f 300 22)
  (GL11/glTexCoord2f (/ 160 512) (/ 66 512)) (GL11/glVertex2f 340 22)
  (GL11/glTexCoord2f (/ 160 512) (/ 44 512)) (GL11/glVertex2f 340 0)
  (GL11/glEnd)
  
)

(defn create-window
  []
  (def display-mode nil)
  (Display/setFullscreen fullscreen?)
  (let [d (Display/getAvailableDisplayModes)]
    (doseq [i d]
      (when (and
              (= screen-w (.getWidth i))
              (= screen-h (.getHeight i))
              (= screen-bpp (.getBitsPerPixel i)))
        (def display-mode i)))
    (when (nil? display-mode)
      (def display-mode (DisplayMode. default-screen-w default-screen-h))))
  (Display/setDisplayMode display-mode)
  (Display/setTitle window-title)
  (Keyboard/enableRepeatEvents true)
  (Display/create))

(defn init
  []
  (create-window)
  (load-textures)
  (init-gl))

(defn load-textures
  []
  (try
    (def texture-font-casual
      (TextureLoader/getTexture
        "PNG"
        (ResourceLoader/getResourceAsStream "img/font-casual.png")))
    (def texture-font-formal
      (TextureLoader/getTexture
        "PNG"
        (ResourceLoader/getResourceAsStream "img/font-formal.png")))
    (def texture-creature
      (TextureLoader/getTexture
        "PNG"
        (ResourceLoader/getResourceAsStream "img/creature.png")))
    (def texture-floor
      (TextureLoader/getTexture
        "PNG"
        (ResourceLoader/getResourceAsStream "img/floor.png")))
    (def texture-side
      (TextureLoader/getTexture
        "PNG"
        (ResourceLoader/getResourceAsStream "img/side.png")))
    (catch java.io.IOException e
      (.printStackTrace e))))

(defn init-gl
  []
  (GL11/glDisable GL11/GL_DEPTH_TEST)
  (GL11/glEnable GL11/GL_BLEND)
  (GL11/glEnable GL11/GL_TEXTURE_2D)
  (GL11/glShadeModel GL11/GL_SMOOTH)
  (GL11/glClearColor 0.5 0.3 0.7 0.0)
  (GL11/glViewport 0 0 screen-w screen-h)
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0 screen-w screen-h 0 1 -1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

(defn clean-up
  []
  (Display/destroy))

