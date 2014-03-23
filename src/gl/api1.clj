(ns gl.api1
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard]))

(def screen-w 400)
(def screen-h 400)
(def application-title "OpenGL API Study 2")

(defn init-gl
  [width height title]
  (try
    (Display/setDisplayMode (DisplayMode. width height))
    (Display/setTitle title)
    (Display/create)
    (Display/setVSyncEnabled true)
    (GL11/glClearColor 0.4 0.4 1.0 1.0)
    (catch LWJGLException e
      (.printStackTrace e))
    ))

(defn render
  []
  (GL11/glColor3d 0.2 1.2 0.2)
  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
  (GL11/glBegin GL11/GL_TRIANGLES)
  (GL11/glVertex2d 0.0 0.5)
  (GL11/glVertex2d -0.5 -0.5)
  (GL11/glVertex2d 0.5 -0.5)
  (GL11/glEnd)
  (GL11/glFlush))

(defn start
  []
  (init-gl screen-w screen-h application-title)
  (while (not (Display/isCloseRequested))
    (render)
    (Display/update)
    (Display/sync 60))
  (Display/destroy))


