(ns gl.api3
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard]))

(def screen-w 400)
(def screen-h 400)
(def application-title "OpenGL API Study 3")

(defn init-gl
  [width height title]
  (try
    (Display/setDisplayMode (DisplayMode. width height))
    (Display/setTitle title)
    (Display/create)
    (Display/setVSyncEnabled true)
    (GL11/glEnable GL11/GL_BLEND)
    (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
    (GL11/glEnable GL11/GL_POINT_SMOOTH)
    (GL11/glEnable GL11/GL_LINE_SMOOTH)
    (GL11/glEnable GL11/GL_POLYGON_SMOOTH)
    (GL11/glClearColor 0.4 0.4 1.0 1.0)
    (catch LWJGLException e
      (.printStackTrace e))))

(defn render
  []
  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
  (GL11/glPointSize 8.0)
  (GL11/glBegin GL11/GL_LINE_LOOP)
  (GL11/glColor3f 1.0 1.0 1.0)
  (GL11/glVertex2f -0.5 -0.5)
  (GL11/glVertex2f 0.5 -0.5)
  (GL11/glVertex2f 0.0 0.5)
  (GL11/glEnd)
  (GL11/glBegin GL11/GL_LINES)
  (dotimes [i 3]
    (GL11/glLineWidth (* i 1.0))
    (GL11/glVertex2f (* 0.1 i) (* 0.1 i))
    (GL11/glVertex2f (+ 0.2 (* 0.1 i)) (+ 0.4 (* 0.1 i))))
  (GL11/glEnd)
  (GL11/glShadeModel GL11/GL_FLAT)
  (GL11/glBegin GL11/GL_TRIANGLE_STRIP)
  (dotimes [i 6]
    (if (zero? (rem i 2))
      (GL11/glColor3f 1.0 0.0 0.0)
      (GL11/glColor3f 0.0 1.0 0.0))
    (GL11/glVertex2f (+ -0.8 (* 0.3 i)) (* 0.4 (if (zero? (rem i 2)) 1 -1))))
  (GL11/glEnd)
  (GL11/glColor4f 1.0 0.0 0.0 0.2)
  (GL11/glRectf -0.8 0.8 0.8 -0.8)
  (GL11/glColor3f 0.0 0.0 1.0)
  (GL11/glBegin GL11/GL_POINTS)
  (GL11/glVertex2f 0.7 -0.2)
  (GL11/glVertex2f 0.5 -0.3)
  (GL11/glEnd))

(defn start
  []
  (init-gl screen-w screen-h application-title)
  (while (not (Display/isCloseRequested))
    (render)
    (Display/update)
    (Display/sync 60))
  (Display/destroy))


