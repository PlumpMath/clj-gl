(ns gl.nehe8
  (:import [java.nio ByteBuffer ByteOrder IntBuffer FloatBuffer])
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU])
  (:import [org.newdawn.slick Color])
  (:import [org.newdawn.slick.opengl Texture TextureLoader])
  (:import [org.newdawn.slick.util ResourceLoader])
  (:gen-class
    :name gl.nehe8
    :methods [#^{:static true} [start [] Void]]))

(def application-on? true)
(def window-title "NeHe's OpenGL Lesson 8 for LWJGL (Blending)")
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
(def filter-n 0)
(def x-rotation 0)
(def y-rotation 0)
(def x-speed 0)
(def y-speed 0)
(def z -5.0)
(def light-ambient (into-array Float/TYPE [0.5 0.5 0.5 1.0]))
(def light-diffuse (into-array Float/TYPE [1.0 1.0 1.0 1.0]))
(def light-position (into-array Float/TYPE [0.0 0.0 2.0 1.0]))
(def textures [nil nil])

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
          Keyboard/KEY_F (def filter-n (rem (+ 1 filter-n) 2))
          Keyboard/KEY_B (switch-blend!)
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
  (GL11/glLoadIdentity) ; Reset The Current Modelview Matrix
  (GL11/glTranslatef 0.0 0.0 z) ; Translate Into/Out Of The Screen By z

  (GL11/glRotatef x-rotation 1.0 0.0 0.0) ; Rotate On The X Axis By x-rotation
  (GL11/glRotatef y-rotation 0.0 1.0 0.0) ; Rotate On The Y Axis By y-rotation
  (.bind (get textures filter-n)) ; Select A Texture Based On filter

  (GL11/glBegin GL11/GL_QUADS) ; Start Drawing Quads
  ;; Front Face
  (GL11/glNormal3f 0.0 0.0 1.0) ; Normal Pointing Towards Viewer
  (GL11/glTexCoord2f 0.0 0.0) (GL11/glVertex3f -1.0 -1.0 1.0) ; Point 1 (Front)
  (GL11/glTexCoord2f 1.0 0.0) (GL11/glVertex3f  1.0 -1.0 1.0) ; Point 2 (Front)
  (GL11/glTexCoord2f 1.0 1.0) (GL11/glVertex3f  1.0  1.0 1.0) ; Point 3 (Front)
  (GL11/glTexCoord2f 0.0 1.0) (GL11/glVertex3f -1.0  1.0 1.0) ; Point 4 (Front)
  ;; Back Face
  (GL11/glNormal3f 0.0 0.0 -1.0) ; Normal Pointing Away From Viewer
  (GL11/glTexCoord2f 1.0 0.0) (GL11/glVertex3f -1.0 -1.0 -1.0) ; Point 1 (Back)
  (GL11/glTexCoord2f 1.0 1.0) (GL11/glVertex3f -1.0  1.0 -1.0) ; Point 2 (Back)
  (GL11/glTexCoord2f 0.0 1.0) (GL11/glVertex3f  1.0  1.0 -1.0) ; Point 3 (Back)
  (GL11/glTexCoord2f 0.0 0.0) (GL11/glVertex3f  1.0 -1.0 -1.0) ; Point 4 (Back)
  ;; Top Face
  (GL11/glNormal3f 0.0 1.0 0.0) ; Normal Pointing Up
  (GL11/glTexCoord2f 0.0 1.0) (GL11/glVertex3f -1.0 1.0 -1.0) ; Point 1 (Top)
  (GL11/glTexCoord2f 0.0 0.0) (GL11/glVertex3f -1.0 1.0  1.0) ; Point 2 (Top)
  (GL11/glTexCoord2f 1.0 0.0) (GL11/glVertex3f  1.0 1.0  1.0) ; Point 3 (Top)
  (GL11/glTexCoord2f 1.0 1.0) (GL11/glVertex3f  1.0 1.0 -1.0) ; Point 4 (Top)
  ;; Bottom Face
  (GL11/glNormal3f 0.0 -1.0 0.0) ; Normal Pointing Down
  (GL11/glTexCoord2f 1.0 1.0) (GL11/glVertex3f -1.0 -1.0 -1.0) ; Point 1 (Bottom)
  (GL11/glTexCoord2f 0.0 1.0) (GL11/glVertex3f  1.0 -1.0 -1.0) ; Point 2 (Bottom)
  (GL11/glTexCoord2f 0.0 0.0) (GL11/glVertex3f  1.0 -1.0  1.0) ; Point 3 (Bottom)
  (GL11/glTexCoord2f 1.0 0.0) (GL11/glVertex3f -1.0 -1.0  1.0) ; Point 4 (Bottom)
  ;; Right face
  (GL11/glNormal3f 1.0 0.0 0.0) ; Normal Pointing Right
  (GL11/glTexCoord2f 1.0 0.0) (GL11/glVertex3f 1.0 -1.0 -1.0) ; Point 1 (Right)
  (GL11/glTexCoord2f 1.0 1.0) (GL11/glVertex3f 1.0  1.0 -1.0) ; Point 2 (Right)
  (GL11/glTexCoord2f 0.0 1.0) (GL11/glVertex3f 1.0  1.0  1.0) ; Point 3 (Right)
  (GL11/glTexCoord2f 0.0 0.0) (GL11/glVertex3f 1.0 -1.0  1.0) ; Point 4 (Right)
  ;; Left Face
  (GL11/glNormal3f -1.0 0.0 0.0) ; Normal Pointing Left
  (GL11/glTexCoord2f 0.0 0.0) (GL11/glVertex3f -1.0 -1.0 -1.0) ; Point 1 (Left)
  (GL11/glTexCoord2f 1.0 0.0) (GL11/glVertex3f -1.0 -1.0  1.0) ; Point 2 (Left)
  (GL11/glTexCoord2f 1.0 1.0) (GL11/glVertex3f -1.0  1.0  1.0) ; Point 3 (Left)
  (GL11/glTexCoord2f 0.0 1.0) (GL11/glVertex3f -1.0  1.0 -1.0) ; Point 4 (Left)
  (GL11/glEnd) ; Done Drawing Quads
)

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
  (init-gl))

(defn load-textures
  []
  (try
    (def textures (assoc textures 0
                         (TextureLoader/getTexture
                           "BMP"
                           (ResourceLoader/getResourceAsStream "Data/Glass.bmp"))))
    (def textures (assoc textures 1
                         (TextureLoader/getTexture
                           "BMP"
                           (ResourceLoader/getResourceAsStream "Data/Glass.bmp"))))
    (catch java.io.IOException e
      (.printStackTrace e)))
  (.bind (textures 0))
  (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MAG_FILTER GL11/GL_NEAREST)
  (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MIN_FILTER GL11/GL_NEAREST)
  (.bind (textures 1))
  (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MAG_FILTER GL11/GL_LINEAR)
  (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MIN_FILTER GL11/GL_LINEAR))

(defn init-gl
  []
  (GL11/glEnable GL11/GL_TEXTURE_2D) ; Enable Texture Mapping
  (GL11/glShadeModel GL11/GL_SMOOTH) ; Enable Smooth Shading
  (GL11/glClearColor 0.0 0.0 0.0 0.0) ; Black Background
  (GL11/glClearDepth 1.0) ; Depth Buffer Setup
  (GL11/glEnable GL11/GL_DEPTH_TEST) ; Enables Depth Testing
  (GL11/glDepthFunc GL11/GL_LEQUAL) ; The Type Of Depth Testing To Do
  (GL11/glMatrixMode GL11/GL_PROJECTION) ; Select The Projection Matrix
  (GL11/glLoadIdentity) ; Reset The Projection Matrix
  ;; Calculate The Aspect Ratio Of The Window
  (GLU/gluPerspective
    45.0
    (/ (.getWidth display-mode) (.getHeight display-mode))
    0.1
    100.0)
  (GL11/glMatrixMode GL11/GL_MODELVIEW) ; Select The Modelview Matrix
  (GL11/glColor4f 1.0 1.0 1.0 0.5) ; Full Brightness, 50% Alpha
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE) ; Blending Function For Translucency Based On Source Alpha Value
  ;; Really Nice Perspective Calculations
  (GL11/glHint GL11/GL_PERSPECTIVE_CORRECTION_HINT GL11/GL_NICEST)
  (let [b (ByteBuffer/allocateDirect 16)]
    (.order b (ByteOrder/nativeOrder))
    (GL11/glLight GL11/GL_LIGHT1 GL11/GL_AMBIENT
                  (.flip (.put (.asFloatBuffer b) light-ambient)))
    (GL11/glLight GL11/GL_LIGHT1 GL11/GL_DIFFUSE
                  (.flip (.put (.asFloatBuffer b) light-diffuse)))
    (GL11/glLight GL11/GL_LIGHT1 GL11/GL_POSITION
                  (.flip (.put (.asFloatBuffer b) light-position))))
  (GL11/glEnable GL11/GL_LIGHT1)
  )

(defn clean-up
  []
  (Display/destroy))

