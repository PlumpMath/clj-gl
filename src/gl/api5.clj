(ns gl.api5
  (:import [org.lwjgl BufferUtils LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL15 GL20 GL30])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU]))

(def screen-w 800)
(def screen-h 800)
(def application-title "OpenGL API Study 5")

(def x-angle 0)
(def y-angle 0)
(def z-angle 0)

(defn into-direct-float-buffer
  [s]
  (let [elt-size 4
        buffer (java.nio.ByteBuffer/allocateDirect (* elt-size (count s)))]
    (dotimes [i (count s)]
      (.putFloat buffer (* i elt-size) (get s i)))
    (.asFloatBuffer buffer)))

(def vert-src
  [0.0 0.0 -0.8
   0.5 0.5 0.0
   0.5 0.5 0.0
   0.0 0.0 -0.8
   -0.5 0.5 0.0
   -0.5 -0.5 0.0
   0.0 0.0 -0.8
   -0.5 -0.5 0.0
   0.5 -0.5 0.0
   0.0 0.0 -0.8
   0.5 -0.5 0.0
   0.5 0.5 0.0])
(def vert-buffer (into-direct-float-buffer vert-src))
(def vert-size (count vert-src))

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
          nil))))
  (Display/setTitle (str x-angle \space y-angle \space z-angle)))

(defn update
  []
  (proc-keyboard))

(defn render
  []
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (GL11/glPolygonMode GL11/GL_FRONT_AND_BACK GL11/GL_SMOOTH)
  (GL11/glShadeModel GL11/GL_SMOOTH)
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glEnableClientState GL11/GL_VERTEX_ARRAY)
  (GL11/glVertexPointer 3 0 vert-buffer)

  (GL11/glPushMatrix)
  (GL11/glRotatef x-angle 1.0 0.0 0.0)
  (GL11/glRotatef y-angle 0.0 1.0 0.0)
  (GL11/glRotatef z-angle 0.0 0.0 1.0)

  (GL11/glRectf -0.5 0.5 0.5 -0.5)
;  (GL11/glBegin GL11/GL_TRIANGLES)
;  (doseq [i (range 0 vert-size 3)]
;    (GL11/glVertex3f (.get vert-buffer i) (.get vert-buffer (inc i)) (.get vert-buffer (+ 2 i))))
  (GL11/glDrawArrays GL11/GL_TRIANGLES 0 12)
  (GL11/glPopMatrix)


;  (GL12/glColor3f 0.1 0.0 0.0)
;  (GL11/glBegin GL11/GL_POLYGON)
;  (GL11/glVertex2f -0.5 0.5)
;  (GL11/glVertex2f -0.9 -0.5)
;  (GL11/glVertex2f -0.1 -0.5)
;  (GL11/glEnd)
;
;  (GL11/glBegin GL11/GL_POLYGON)
;  (GL11/glVertex2f 0.5 0.5)
;  (GL11/glVertex2f 0.9 -0.5)
;  (GL11/glVertex2f 0.1 -0.5)
;  (GL11/glEnd)
  )

(defn start
  []
  (init-gl screen-w screen-h application-title)
  (while (not (Display/isCloseRequested))
    (update)
    (render)
    (Display/update)
    (Display/sync 60))
  (Display/destroy))


