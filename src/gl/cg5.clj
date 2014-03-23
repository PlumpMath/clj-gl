;;;; interactive computer graphics
;;;; exercise 2.1
(ns gl.cg5
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.opengl Display DisplayMode GL11]))

(def last-frame 0)
(def fps 0)
(def last-fps 0)

(def vtxs [[-50.0 -50.0] [0.0 50.0] [50.0 -50.0]])
(def n 7)

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

(defn half-and-distort
  [a b k]
  (if (< 0 k)
    (let [ab [(* (/ (+ (a 0) (b 0)) 2) (+ 0.5 (rand))) (* (/ (+ (a 1) (b 1)) 2) (+ 0.5 (rand)))]]
      (half-and-distort a ab (dec k))
      (half-and-distort ab b (dec k)))
    (do
      (GL11/glVertex2f (a 0) (a 1))
      (GL11/glVertex2f (b 0) (b 1)))))

(defn divide-triangle
  [a b c k]
  (half-and-distort a b k)
  (half-and-distort b c k)
  (half-and-distort c a k))

(defn render-gl
  []
  ; Clear The Screen And The Depth Buffer
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT
                        GL11/GL_DEPTH_BUFFER_BIT))
  ; R G B A Set The Color
  (GL11/glColor3f 1.0 1.0 1.0)
  ; draw pixel
  (GL11/glBegin GL11/GL_POLYGON)
  (divide-triangle (vtxs 0) (vtxs 1) (vtxs 2) n)
  (GL11/glEnd)
  (GL11/glFlush))

(defn update
  [delta]
  ; update fps counter
  (update-fps))

(defn run
  []
  (while (not (Display/isCloseRequested))
    (update (get-delta))
    (render-gl)
    (Display/update)
    (Display/sync 1))
  (Display/destroy))

(defn init-gl
  []
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho -100 100 -100 100 1 -1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

(defn init
  []
  (try
    (Display/setDisplayMode (DisplayMode. 800 800))
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


