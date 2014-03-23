;;;; sierpinski example
(ns gl.cg3
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.opengl Display DisplayMode GL11]))

(def last-frame 0)
(def fps 0)
(def last-fps 0)

(def vtxs [[0.0 0.0 0.0] [20.0 50.0 8.0] [50.0 5.0 25.0] [30.0 12.0 25.0]])
(def start-vtx [25.0 10.0 25.0])

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

(defn render-gl
  []
  ; draw pixel(let [triangle [[0.0 0.0] [300.0 600.0] [600.0 0.0]]
  (GL11/glBegin GL11/GL_POINTS)
  (loop [i 0 vtx start-vtx]
    (when (< i 400)
      (let [rand-pick (rand-int 4)
            new-vtx [(/ (+ (get vtx 0) (get-in vtxs [rand-pick 0])) 2.0)
                     (/ (+ (get vtx 1) (get-in vtxs [rand-pick 1])) 2.0)
                     (/ (+ (get vtx 2) (get-in vtxs [rand-pick 2])) 2.0)]]
        (GL11/glColor3f (/ (+ 50 (get vtx 0)) 250.0)
                        (/ (+ 50 (get vtx 1)) 250.0)
                        (/ (+ 50 (get vtx 2)) 250.0))
        (GL11/glVertex3f (vtx 0) (vtx 1) (vtx 2))
        (recur (inc i) new-vtx))))
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

