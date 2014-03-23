(ns gl.api4
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard]))

(def screen-w 800)
(def screen-h 800)
(def application-title "OpenGL API Study 4")

(def x-angle 0)
(def y-angle 0)
(def z-angle 0)

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
  (GL11/glShadeModel GL11/GL_SMOOTH)

  (GL11/glPushMatrix)
  (GL11/glRotatef x-angle 1.0 0.0 0.0)
  (GL11/glRotatef y-angle 0.0 1.0 0.0)
  (GL11/glRotatef z-angle 0.0 0.0 1.0)
  (GL11/glPolygonMode GL11/GL_FRONT_AND_BACK GL11/GL_SMOOTH)


  ; 아랫면 흰 바닥
  (GL11/glColor3f 1.0 1.0 1.0)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glVertex2f -0.5 0.5)
  (GL11/glVertex2f 0.5 0.5)
  (GL11/glVertex2f 0.5 -0.5)
  (GL11/glVertex2f -0.5 -0.5)
  (GL11/glEnd)

  ; 위쪽 빨간 변
  (GL11/glBegin GL11/GL_TRIANGLE_FAN)
  (GL11/glColor3f 1.0 1.0 1.0)
  (GL11/glVertex3f 0.0 0.0 -0.8)
  (GL11/glColor3f 1.0 0.0 0.0)
  (GL11/glVertex2f 0.5 0.5)
  (GL11/glVertex2f -0.5 0.5)

  ; 왼쪽 노란 변
  (GL11/glColor3f 1.0 1.0 0)
  (GL11/glVertex2f -0.5 -0.5)
  
  ; 아래쪽 초록 변
  (GL11/glColor3f 0.0 1.0 0.0)
  (GL11/glVertex2f 0.5 -0.5)

  ; 오른쪽 파란 변
  (GL11/glColor3f 0.0 0.0 1.0)
  (GL11/glVertex2f 0.5 0.5)
  
  (GL11/glEnd)
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


