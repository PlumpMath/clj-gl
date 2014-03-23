;;;; fractal triangle
(ns gl.cg4
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.opengl Display DisplayMode GL11]))

(def last-frame 0)
(def fps 0)
(def last-fps 0)

(def colors [[0.2 0.2 0.2] [0.4 0.4 0.4] [0.7 0.7 0.7] [0.9 0.7 0.6]])
(def vtxs [[0.0 0.0 0.0] [20.0 50.0 8.0] [50.0 5.0 25.0] [30.0 12.0 25.0]])
(def n 4)

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

(defn triangle
  [a b c]
  (GL11/glVertex3f (a 0) (a 1) (a 2))
  (GL11/glVertex3f (b 0) (b 1) (b 2))
  (GL11/glVertex3f (c 0) (c 1) (c 2)))

(defn tetra
  [a b c d]
  (GL11/glColor3f (get-in colors [0 0]) (get-in colors [0 1]) (get-in colors [0 2]))
  (triangle a b c)
  (GL11/glColor3f (get-in colors [1 0]) (get-in colors [1 1]) (get-in colors [1 2]))
  (triangle a c d)
  (GL11/glColor3f (get-in colors [2 0]) (get-in colors [2 1]) (get-in colors [2 2]))
  (triangle a d b)
  (GL11/glColor3f (get-in colors [3 0]) (get-in colors [3 1]) (get-in colors [3 2]))
  (triangle b d c))

(defn divide-tetra
  [a b c d m]
  (if (< 0 m)
    (let [ab [(/ (+ (a 0) (b 0)) 2) (/ (+ (a 1) (b 1)) 2) (/ (+ (a 2) (b 2)) 2)]
          ac [(/ (+ (a 0) (c 0)) 2) (/ (+ (a 1) (c 1)) 2) (/ (+ (a 2) (c 2)) 2)]
          ad [(/ (+ (a 0) (d 0)) 2) (/ (+ (a 1) (d 1)) 2) (/ (+ (a 2) (d 2)) 2)]
          bc [(/ (+ (b 0) (c 0)) 2) (/ (+ (b 1) (c 1)) 2) (/ (+ (b 2) (c 2)) 2)]
          cd [(/ (+ (c 0) (d 0)) 2) (/ (+ (c 1) (d 1)) 2) (/ (+ (c 2) (d 2)) 2)]
          bd [(/ (+ (b 0) (d 0)) 2) (/ (+ (b 1) (d 1)) 2) (/ (+ (b 2) (d 2)) 2)]]
      (divide-tetra a ab ac ad (dec m))
      (divide-tetra ab b bc bd (dec m))
      (divide-tetra ac bc c cd (dec m))
      (divide-tetra ad cd d bd (dec m)))
    (tetra a b c d)))

(defn render-gl
  []
  ; Clear The Screen And The Depth Buffer
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT
                        GL11/GL_DEPTH_BUFFER_BIT))
  ; draw pixel
  (GL11/glBegin GL11/GL_TRIANGLES)
  (divide-tetra (vtxs 0) (vtxs 1) (vtxs 2) (vtxs 3) n)
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
    (Display/sync 60))
  (Display/destroy))

(defn init-gl
  []
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0.0 50.0 0.0 50.0 -50.0 50.0)
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glEnable GL11/GL_DEPTH_TEST))

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

