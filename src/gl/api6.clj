(ns gl.api6
  (:import [org.lwjgl BufferUtils LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL15 GL20 GL30])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU]))

(def screen-w 800)
(def screen-h 800)
(def application-title "OpenGL API Study 6")

(def x-angle 0)
(def y-angle 0)
(def z-angle 0)

(defn prepare-polygons
  []
  (def dl (GL11/glGenLists 1))
  (GL11/glNewList dl GL11/GL_COMPILE)
  (GL11/glBegin (GL11/GL_TRIANGLES))
  (GL11/glVertex2f 0.0 0.2)
  (GL11/glVertex2f -0.2 -0.2)
  (GL11/glVertex2f 0.2 -0.2)
  (GL11/glEnd)
  (GL11/glEndList))

(defn init-gl
  [width height title]
  (try
    (Display/setDisplayMode (DisplayMode. width height))
    (Display/setTitle title)
    (Display/create)
    (Display/setVSyncEnabled true)
    (GL11/glEnable GL11/GL_BLEND)
    (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
    (GL11/glClearColor 0.4 0.4 1.0 1.0)
    (Keyboard/enableRepeatEvents true)
    (catch LWJGLException e
      (.printStackTrace e))))

(defn proc-keyboard
  []
  (while (Keyboard/next)
    (let [key-state (Keyboard/getEventKey)]
      (when (Keyboard/getEventKeyState)
        (condp = key-state
          Keyboard/KEY_A (def y-angle (+ y-angle 2))
          Keyboard/KEY_D (def y-angle (- y-angle 2))
          Keyboard/KEY_W (def x-angle (+ x-angle 2))
          Keyboard/KEY_S (def x-angle (- x-angle 2))
          Keyboard/KEY_Q (def z-angle (+ z-angle 2))
          Keyboard/KEY_E (def z-angle (- z-angle 2))
          Keyboard/KEY_Z (do (def x-angle 0) (def y-angle 0) (def z-angle 0))
          Keyboard/KEY_1 (GL11/glEnable GL11/GL_DEPTH_TEST)
          Keyboard/KEY_2 (GL11/glDisable GL11/GL_DEPTH_TEST)
          Keyboard/KEY_3 (GL11/glEnable GL11/GL_CULL_FACE)
          Keyboard/KEY_4 (GL11/glDisable GL11/GL_CULL_FACE)
          nil)))))

(defn update
  []
  (proc-keyboard))

(defn render
  []
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))

  (GL11/glMatrixMode (GL11/GL_MODELVIEW))
  (GL11/glLoadIdentity)

  (GL11/glColor3f 1.0 0.0 0.0)
  (GL11/glCallList dl)

  (GL11/glTranslatef 0.2 0.0 0.0)
  (GL11/glColor3f 0.0 1.0 0.0)
  (GL11/glCallList dl)

  (GL11/glTranslatef 0.2 0.0 0.0)
  (GL11/glColor3f 0.0 0.0 1.0)
  (GL11/glCallList dl))

(defn start
  []
  (init-gl screen-w screen-h application-title)
  (prepare-polygons)
  (while (not (Display/isCloseRequested))
    (update)
    (render)
    (Display/update)
    (Display/sync 60))
  (Display/destroy))


