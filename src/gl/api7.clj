;;;; 변환

(ns gl.api7
  (:import [org.lwjgl BufferUtils LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL15 GL20 GL30])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU]))

(def screen-w 800)
(def screen-h 800)
(def application-title "OpenGL API Study 7")

(def x-angle 0)
(def y-angle 0)
(def z-angle 0)
(def ex 0)
(def ey 0)
(def ez 0)
(def left -1)
(def right 1)
(def bottom -1)
(def top 1)
(def near -1)
(def far 1)
(def fov 60)
(def projection :ortho)

(defn prepare-draw-list
  []
  (def draw-list-size 1)
  (def draw-list (GL11/glGenLists draw-list-size))
  (def draw-list-pyramid (+ draw-list 0))
  ; 피라미드 : draw-data+1
  (GL11/glNewList (+ draw-list 0) GL11/GL_COMPILE)
  ; 바닥
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glColor3f 1.0 0.0 0.0)
  (GL11/glVertex2f -0.5 0.5)
  (GL11/glColor3f 0.0 0.0 1.0)
  (GL11/glVertex2f 0.5 0.5)
  (GL11/glColor3f 0.0 1.0 0.0)
  (GL11/glVertex2f 0.5 -0.5)
  (GL11/glColor3f 1.0 1.0 0.0)
  (GL11/glVertex2f -0.5 -0.5)
  (GL11/glEnd)
  ; 위쪽
  (GL11/glBegin GL11/GL_TRIANGLE_FAN)
  (GL11/glColor3f 1.0 1.0 1.0)
  (GL11/glVertex3f 0.0 0.0 0.8)
  (GL11/glColor3f 0.0 0.0 1.0)
  (GL11/glVertex2f 0.5 0.5)
  (GL11/glColor3f 1.0 0.0 0.0)
  (GL11/glVertex2f -0.5 0.5)
  ; 왼쪽
  (GL11/glColor3f 1.0 1.0 0)
  (GL11/glVertex2f -0.5 -0.5)
  ; 아래쪽
  (GL11/glColor3f 0.0 1.0 0.0)
  (GL11/glVertex2f 0.5 -0.5)
  ; 오른쪽
  (GL11/glColor3f 0.0 0.0 1.0)
  (GL11/glVertex2f 0.5 0.5)
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
    (GL11/glOrtho left right bottom top near far)
    (Keyboard/enableRepeatEvents true)
    (catch LWJGLException e
      (.printStackTrace e))))

(defn proc-keyboard
  []
  (while (Keyboard/next)
    (let [key-state (Keyboard/getEventKey)]
      (when (Keyboard/getEventKeyState)
        (condp = key-state
          Keyboard/KEY_1 (GL11/glEnable GL11/GL_DEPTH_TEST)
          Keyboard/KEY_2 (GL11/glDisable GL11/GL_DEPTH_TEST)
          Keyboard/KEY_3 (GL11/glEnable GL11/GL_CULL_FACE)
          Keyboard/KEY_4 (GL11/glDisable GL11/GL_CULL_FACE)
          Keyboard/KEY_A (def y-angle (+ y-angle 5))
          Keyboard/KEY_D (def y-angle (- y-angle 5))
          Keyboard/KEY_W (def x-angle (+ x-angle 5))
          Keyboard/KEY_S (def x-angle (- x-angle 5))
          Keyboard/KEY_Q (def z-angle (+ z-angle 5))
          Keyboard/KEY_E (def z-angle (- z-angle 5))
          Keyboard/KEY_Z (do (def x-angle 0) (def y-angle 0) (def z-angle 0))
          Keyboard/KEY_NUMPAD4 (def ex (+ ex 0.1))
          Keyboard/KEY_NUMPAD6 (def ex (- ex 0.1))
          Keyboard/KEY_NUMPAD8 (def ey (+ ey 0.1))
          Keyboard/KEY_NUMPAD2 (def ey (- ey 0.1))
          Keyboard/KEY_NUMPAD7 (def ez (+ ez 0.1))
          Keyboard/KEY_NUMPAD9 (def ez (- ez 0.1))
          Keyboard/KEY_NUMPAD0 (do (def ex 0) (def ey 0) (def ez 0))
          Keyboard/KEY_F1 (def projection :ortho)
          Keyboard/KEY_F2 (def projection :frustum)
          Keyboard/KEY_F3 (def projection :perspective)
          Keyboard/KEY_R (def left (+ left 0.1))
          Keyboard/KEY_F (def left (- left 0.1))
          Keyboard/KEY_T (def right (+ right 0.1))
          Keyboard/KEY_G (def right (- right 0.1))
          Keyboard/KEY_Y (def bottom (- bottom 0.1))
          Keyboard/KEY_H (def bottom (+ bottom 0.1))
          Keyboard/KEY_U (def top (- top 0.1))
          Keyboard/KEY_J (def top (+ top 0.1))
          Keyboard/KEY_I (def near (- near 0.1))
          Keyboard/KEY_K (def near (+ near 0.1))
          Keyboard/KEY_O (def far (- far 0.1))
          Keyboard/KEY_L (def far (+ far 0.1))
          Keyboard/KEY_P (def fov (- fov 1))
          Keyboard/KEY_SEMICOLON (def fov (+ fov 1))
          Keyboard/KEY_V (do
                           (def left -1) (def right 1)
                           (def bottom -1) (def top 1)
                           (if (= projection :ortho)
                             (do (def near -1) (def far 1))
                             (do (def near 1) (def far 50))))
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

  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (condp = projection
    :ortho (GL11/glOrtho left right bottom top near far)
    :frustum (GL11/glFrustum left right bottom top near far)
    :perspective (GLU/gluPerspective fov (/ screen-w screen-h) near far)
    nil)
;  (GLU/gluLookAt ex ey ez 0.0 0.0 -1.0 0.0 1.0 0.0)

  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glPushMatrix)
  (when-not (= projection :ortho)
    (GL11/glTranslatef 0.0 0.0 -2))
  (GL11/glRotatef x-angle 1.0 0.0 0.0)
  (GL11/glRotatef y-angle 0.0 1.0 0.0)
  (GL11/glRotatef z-angle 0.0 0.0 1.0)
  (GL11/glCallList draw-list-pyramid)
  (GL11/glPopMatrix)

  )

(defn start
  []
  (init-gl screen-w screen-h application-title)
  (prepare-draw-list)
  (while (not (Display/isCloseRequested))
    (update)
    (render)
    (Display/update))
  (Display/destroy))


