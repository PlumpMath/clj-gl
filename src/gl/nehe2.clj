(ns gl.nehe2
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU]))

(def release? false)
(def done? false)
(def window-title "NeHe's OpenGL Lesson 2 for LWJGL (Your First Polygon)")
(def f1? false)
(def fullscreen? false)
(def display-mode nil)
(def default-screen-w 640)
(def default-screen-h 480)
(def default-screen-bpp 24)
(def screen-w 800)
(def screen-h 600)
(def screen-bpp 32)


(declare -main run mainloop switch-mode render create-window init init-gl cleanup exit-application)

(defn -main
  [& args]
  (def done? false)
  (if (and (not (nil? args))
           (when (= (-> args first .toLowerCase) "fullscreen")))
    (def fullscreen? true)
    (def fullscreen? false))
  (run))

(defn run
  []
  (try
    (init)
    (while (not done?)
      (mainloop)
      (render)
      (Display/update))
    (cleanup)
    (catch Exception e
      (.printStackTrace e)
      (exit-application))))

(defn mainloop
  []
  (when (Keyboard/isKeyDown Keyboard/KEY_ESCAPE)
    (def done? true))
  (when (Display/isCloseRequested)
    (def done? true))
  (when (Keyboard/isKeyDown Keyboard/KEY_F1)
    (if (not f1?)
      (do
        (def f1? true)
        (switch-mode))
      (def f1? false))))

(defn switch-mode
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
  (GL11/glTranslatef -1.5 0.0 -6.0)
  (GL11/glBegin GL11/GL_TRIANGLES)
  (GL11/glVertex3f 0.0 1.0 0.0)
  (GL11/glVertex3f -1.0 -1.0 0.0)
  (GL11/glVertex3f 1.0 -1.0 0.0)
  (GL11/glEnd)
  (GL11/glTranslatef 3.0 0.0 0.0)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glVertex3f -1.0 1.0 0.0)
  (GL11/glVertex3f 1.0 1.0 0.0)
  (GL11/glVertex3f 1.0 -1.0 0.0)
  (GL11/glVertex3f -1.0 -1.0 0.0)
  (GL11/glEnd))

(defn create-window
  []
  (Display/setFullscreen fullscreen?)
  (let [d (Display/getAvailableDisplayModes)]
    (doseq [i d]
      (when (and
              (= screen-w (.getWidth i))
              (= screen-h (.getHeight i))
              (= screen-bpp (.getBitsPerPixel i)))
        (def display-mode i)))
    (when (nil? display-mode)
      (def display-mode (DisplayMode. default-screen-w default-screen-h)))
    (Display/setDisplayMode display-mode)
    (Display/setTitle window-title)
    (Display/create)))

(defn init
  []
  (create-window)
  (init-gl))

(defn init-gl
  []
  (GL11/glEnable GL11/GL_TEXTURE_2D)
  (GL11/glShadeModel GL11/GL_SMOOTH)
  (GL11/glClearColor 0.0 0.0 0.0 0.0)
  (GL11/glClearDepth 1.0)
  (GL11/glEnable GL11/GL_DEPTH_TEST)
  (GL11/glDepthFunc GL11/GL_LEQUAL)
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GLU/gluPerspective
    45.0
    (/ (.getWidth display-mode) (.getHeight display-mode))
    0.1
    100.0)
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glHint GL11/GL_PERSPECTIVE_CORRECTION_HINT GL11/GL_NICEST))

(defn cleanup
  []
  (Display/destroy))


(defn exit-application
  []
  (when release?
    (System/exit 0)))




