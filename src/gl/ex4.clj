(ns gl.ex4
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.opengl Display DisplayMode GL11]))

(def x 400.0)
(def y 300.0)
(def rotation 0.0)
(def last-frame 0)
(def fps 0)
(def last-fps 0)

(defn get-time
  []
  (/ (* (Sys/getTime) 1000) (Sys/getTimerResolution)))

(defn get-delta
  []
  (let [t (get-time)
        delta (- t last-frame)]
    (def last-frame t)
    delta))

(defn update-fps
  []
  (when (> (- (get-time) last-fps) 1000)
    (Display/setTitle (str "FPS: " fps))
    (def fps 0)
    (def last-fps (+ last-fps 1000)))
  (def fps (inc fps)))

(defn init-gl
  []
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0 800 0 600 1 -1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

(defn render-gl
  []
  ; Clear The Screen And The Depth Buffer
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT
                        GL11/GL_DEPTH_BUFFER_BIT))
  ; R G B A Set The Color To Blue One Time Only
  (GL11/glColor3f 0.5 0.5 1.0)
  ; draw quad
  (GL11/glPushMatrix)
  (GL11/glTranslatef x y 0)
  (GL11/glRotatef rotation 0.0 0.0 1.0)
  (GL11/glTranslatef (- x) (- y) 0)
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glVertex2f (- x 50) (- y 50))
  (GL11/glVertex2f (+ x 50) (- y 60))
  (GL11/glVertex2f (+ x 50) (+ y 50))
  (GL11/glVertex2f (- x 50) (+ y 50))
  (GL11/glEnd)
  (GL11/glPopMatrix))

(defn update
  [delta]
  ; rotate quad
  (def rotation (+ rotation (* 0.15 delta)))
  (when (Keyboard/isKeyDown Keyboard/KEY_LEFT)  (def x (- x (* 0.35 delta))))
  (when (Keyboard/isKeyDown Keyboard/KEY_RIGHT) (def x (+ x (* 0.35 delta))))
  (when (Keyboard/isKeyDown Keyboard/KEY_UP)    (def y (- y (* 0.35 delta))))
  (when (Keyboard/isKeyDown Keyboard/KEY_DOWN)  (def y (+ y (* 0.35 delta))))
  ; keep quad on the screen
  (when (< x 0)   (def x 0))
  (when (> x 800) (def x 800))
  (when (< y 0)   (def y 0))
  (when (> y 600) (def y 600))
  ; update fps counter
  (update-fps))

(defn run
  []
  (while (not (Display/isCloseRequested))
    (update (get-delta))
    (render-gl)
    (Display/update)
    (Display/sync 60))
  (Display/destroy))

(defn init
  []
  (try
    (Display/setDisplayMode (DisplayMode. 800 600))
    (Display/create)
    (catch LWJGLException e
      (.printStackTrace e)
      (System/exit 0)))
  (init-gl)
  (get-delta)
  (def last-fps (get-time)))

(defn -main
  [& rest]
  (init)
  (run))



