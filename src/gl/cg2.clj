;;;; fractal triangle
(ns gl.cg2
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.opengl Display DisplayMode GL11]))

(def last-frame 0)
(def fps 0)
(def last-fps 0)

(def vtxs [[0.0 0.0] [25.0 50.0] [50.0 0.0]])
(def n 5)

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
  (GL11/glVertex2f (a 0) (a 1))
  (GL11/glVertex2f (b 0) (b 1))
  (GL11/glVertex2f (c 0) (c 1)))

(defn divide-triangle
  [a b c k]
  (if (< 0 k)
    (let [ab [(/ (+ (a 0) (b 0)) 2) (/ (+ (a 1) (b 1)) 2)]
          ac [(/ (+ (a 0) (c 0)) 2) (/ (+ (a 1) (c 1)) 2)]
          bc [(/ (+ (b 0) (c 0)) 2) (/ (+ (b 1) (c 1)) 2)]]
      (divide-triangle a ab ac (dec k))
      (divide-triangle c ac bc (dec k))
      (divide-triangle b bc ab (dec k)))
    (triangle a b c)))

(defn render-gl
  []
  ; Clear The Screen And The Depth Buffer
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT
                        GL11/GL_DEPTH_BUFFER_BIT))
  ; R G B A Set The Color
  (GL11/glColor3f 1.0 1.0 1.0)
  ; draw pixel
  (GL11/glBegin GL11/GL_TRIANGLES)
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
    (Display/sync 60))
  (Display/destroy))

(defn init-gl
  []
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0 50 0 50 1 -1)
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

