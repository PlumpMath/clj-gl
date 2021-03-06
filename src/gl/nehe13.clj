(ns gl.nehe9
  (:import [java.nio ByteBuffer ByteOrder IntBuffer FloatBuffer])
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU])
  (:import [org.newdawn.slick Color])
  (:import [org.newdawn.slick.opengl Texture TextureLoader])
  (:import [org.newdawn.slick.util ResourceLoader])
  (:gen-class
    :name gl.nehe9
    :methods [#^{:static true} [start [] Void]]))

(def application-on? true)
(def window-title "NeHe's OpenGL Lesson 9 for LWJGL (Moving Bitmaps in 3D Space)")
(def fullscreen? false)
(def display-mode nil)
(def default-screen-w 640)
(def default-screen-h 480)
(def default-screen-bpp 24)
(def screen-w 800)
(def screen-h 600)
(def screen-bpp 24)

(def fps-limit 60)
(def last-frame-t 0)
(def light? false)
(def blend? false)
(def texture-n 0)
(def number-of-textures 2)
(def x-rotation 0)
(def y-rotation 0)
(def x-speed 0)
(def y-speed 0)
(def z -5.0)
(def light-ambient (into-array Float/TYPE [0.5 0.5 0.5 1.0]))
(def light-diffuse (into-array Float/TYPE [1.0 1.0 1.0 1.0]))
(def light-position (into-array Float/TYPE [0.0 0.0 2.0 1.0]))
(def textures [nil nil])

(def twinkle false)
(def number-of-stars 50)
(def zoom -15.0)
(def tilt 90.0)
(def spin 0.0)
(defrecord Star [r g b dist angle])
(def stars (vec (repeat number-of-stars (Star. 0 0 0 0 0))))


(declare run main-loop switch-mode! switch-light! switch-blend! render create-window
         process-key-event update get-time get-delta
         init init-gl load-textures clean-up)

(defn get-time
  []
  (/ (* (Sys/getTime) 1000) (Sys/getTimerResolution)))

(defn get-delta
  []
  (let [t (get-time)
        delta (- t last-frame-t)]
    (def last-frame-t t)
    delta))

(defn -start
  []
  (def application-on? true)
  (run))

(defn run
  []
  (try
    (init)
    (while application-on?
      (main-loop)
      (render)
      (Display/update))
    (clean-up)
    (catch Exception e
      (.printStackTrace e))))

(defn switch-light!
  []
  (def light? (not light?))
  (if light?
    (GL11/glEnable GL11/GL_LIGHTING)
    (GL11/glDisable GL11/GL_LIGHTING)))

(defn switch-blend!
  []
  (def blend? (not blend?))
  (if blend?
    (do
      (GL11/glEnable GL11/GL_BLEND)
      (GL11/glDisable GL11/GL_DEPTH_TEST))
    (do
      (GL11/glDisable GL11/GL_BLEND)
      (GL11/glEnable GL11/GL_DEPTH_TEST))))

(defn process-key-event
  []
  (while (Keyboard/next)
    (let [key-state (Keyboard/getEventKey)]
      (when (Keyboard/getEventKeyState)
        (condp = key-state
          Keyboard/KEY_F1 (switch-mode!)
          Keyboard/KEY_ESCAPE (def application-on? false)
          Keyboard/KEY_L (switch-light!)
          Keyboard/KEY_F (def texture-n (rem (+ 1 texture-n) number-of-textures))
          Keyboard/KEY_B (switch-blend!)
          Keyboard/KEY_T (def twinkle (not twinkle))
          Keyboard/KEY_PRIOR (def z (- z 0.02))
          Keyboard/KEY_NEXT (def z (+ z 0.02))
          Keyboard/KEY_UP (def x-speed (- x-speed 0.01))
          Keyboard/KEY_DOWN (def x-speed (+ x-speed 0.01))
          Keyboard/KEY_LEFT (def y-speed (- y-speed 0.01))
          Keyboard/KEY_RIGHT (def y-speed (+ y-speed 0.01))
          nil)))))

(defn apply-rotation
  [delta]
  (def x-rotation (+ x-rotation (* x-speed delta)))
  (def y-rotation (+ y-rotation (* y-speed delta))))

(defn update
  [delta]
  (apply-rotation delta))

(defn main-loop
  []
  (process-key-event)
  (update (get-delta))
  (when (Display/isCloseRequested)
    (def application-on? false))
  (Display/sync fps-limit))

(defn switch-mode!
  []
  (def fullscreen? (not fullscreen?))
  (try
    (Display/setFullscreen fullscreen?)
    (catch Exception e
      (.printStackTrace e))))

(defn render
  []
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))

  (.bind (get textures texture-n))
  (dotimes [i number-of-stars]
    (let [star (get stars i)
          diff (get stars (- number-of-stars i 1))]
      (GL11/glLoadIdentity) ; Reset The View Before We Draw Each Star
      (GL11/glTranslatef 0.0 0.0 zoom) ; Zoom Into The Screen (Using The Value In 'zoom')
      (GL11/glRotatef tilt 1.0 0.0 0.0) ; Tilt The View (Using The Value In 'tilt')
      (GL11/glRotatef (:angle star) 0.0 1.0 0.0) ; Rotate To The Current Stars Angle
      (GL11/glTranslatef (:dist star) 0.0 0.0) ; Mvoe Foward On The X Plane
      (GL11/glRotatef (- (:angle star)) 0.0 1.0 0.0) ; Cancel The Current Stars Angle
      (GL11/glRotatef (- tilt) 1.0 0.0 0.0) ; Cancel The Screen Tilt

      (when twinkle
        (GL11/glColor4f (:r diff) (:g diff) (:b diff) 1.0)
        (GL11/glBegin GL11/GL_QUADS)
        (GL11/glTexCoord2f 0.0 0.0)
        (GL11/glVertex3f -1.0 -1.0 0.0)
        (GL11/glTexCoord2f 1.0 0.0)
        (GL11/glVertex3f 1.0 -1.0 0.0)
        (GL11/glTexCoord2f 1.0 1.0)
        (GL11/glVertex3f 1.0 1.0 0.0)
        (GL11/glTexCoord2f 0.0 1.0)
        (GL11/glVertex3f -1.0 1.0 0.0)
        (GL11/glEnd))

      (GL11/glRotatef spin 0.0 0.0 1.0)
      (GL11/glColor4f (:r star) (:g star) (:b star) 1.0)
      (GL11/glBegin GL11/GL_QUADS)
      (GL11/glTexCoord2f 0.0 0.0)
      (GL11/glVertex3f -1.0 -1.0 0.0)
      (GL11/glTexCoord2f 1.0 0.0)
      (GL11/glVertex3f 1.0 -1.0 0.0)
      (GL11/glTexCoord2f 1.0 1.0)
      (GL11/glVertex3f 1.0 1.0 0.0)
      (GL11/glTexCoord2f 0.0 1.0)
      (GL11/glVertex3f -1.0 1.0 0.0)
      (GL11/glEnd)

      (def spin (+ spin 0.01))
      (def stars (assoc-in stars [i :angle] (+ (:angle star) (/ i number-of-stars))))
      (def stars (assoc-in stars [i :dist] (+ (:dist star) -0.01)))
      (when (< (get-in stars [i :dist]) 0.0)
        (def stars (assoc-in stars [i :dist] (+ (get-in stars [i :dist]) 5.0)))
        (def stars (assoc-in stars [i :r] (rand)))
        (def stars (assoc-in stars [i :g] (rand)))
        (def stars (assoc-in stars [i :b] (rand)))))))

(defn create-window
  []
  (def display-mode nil)
  (Display/setFullscreen fullscreen?)
  (let [d (Display/getAvailableDisplayModes)]
    (doseq [i d]
      (when (and
              (= screen-w (.getWidth i))
              (= screen-h (.getHeight i))
              (= screen-bpp (.getBitsPerPixel i)))
        (def display-mode i)))
    (when (nil? display-mode)
      (def display-mode (DisplayMode. default-screen-w default-screen-h))))
  (Display/setDisplayMode display-mode)
  (Display/setTitle window-title)
  (Keyboard/enableRepeatEvents true)
  (Display/create))

(defn init
  []
  (create-window)
  (load-textures)
  (init-gl)
  (dotimes [i number-of-stars]
    (def stars (assoc-in stars [i :angle] 0.0))
    (def stars (assoc-in stars [i :dist] (* (/ i number-of-stars) 5.0)))
    (def stars (assoc-in stars [i :r] (rand)))
    (def stars (assoc-in stars [i :g] (rand)))
    (def stars (assoc-in stars [i :b] (rand)))))

(defn load-textures
  []
  (try
    (def textures (assoc textures 0
                         (TextureLoader/getTexture
                           "BMP"
                           (ResourceLoader/getResourceAsStream "Data/Star.bmp"))))
    (def textures (assoc textures 1
                         (TextureLoader/getTexture
                           "BMP"
                           (ResourceLoader/getResourceAsStream "Data/Glass.bmp"))))
    (catch java.io.IOException e
      (.printStackTrace e))))

(defn init-gl
  []
  (GL11/glEnable GL11/GL_TEXTURE_2D) ; Enable Texture Mapping
  (GL11/glShadeModel GL11/GL_SMOOTH) ; Enable Smooth Shading
  (GL11/glClearColor 0.0 0.0 0.0 0.0) ; Black Background
  (GL11/glClearDepth 1.0) ; Depth Buffer Setup
   ;; Really Nice Perspective Calculations
  (GL11/glHint GL11/GL_PERSPECTIVE_CORRECTION_HINT GL11/GL_NICEST)
  (GL11/glMatrixMode GL11/GL_PROJECTION) ; Select The Projection Matrix
  (GL11/glLoadIdentity) ; Reset The Projection Matrix
  ;; Calculate The Aspect Ratio Of The Window
  (GLU/gluPerspective
    45.0
    (/ (.getWidth display-mode) (.getHeight display-mode))
    0.1
    100.0)
  (GL11/glMatrixMode GL11/GL_MODELVIEW) ; Select The Modelview Matrix
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE) ; Blending Function For Translucency Based On Source Alpha Value
  (GL11/glEnable GL11/GL_BLEND)
  )

(defn clean-up
  []
  (Display/destroy))

