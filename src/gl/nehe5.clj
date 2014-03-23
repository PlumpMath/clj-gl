(ns gl.nehe5
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU]))

(def release? false)
(def done? false)
(def window-title "NeHe's OpenGL Lesson 5 for LWJGL (3D Shapes)")
(def f1? false)
(def fullscreen? false)
(def display-mode nil)
(def default-screen-w 640)
(def default-screen-h 480)
(def default-screen-bpp 24)
(def screen-w 800)
(def screen-h 600)
(def screen-bpp 24)


(def rtri 0)
(def rquad 0)


(declare -main run mainloop switch-mode render create-window init init-gl cleanup exit-application)

(defn -main
  [& args]
  (def done? false)
  (if (and (not (nil? args))
           (when (= (-> args first .toLowerCase) "fullscreen")))
    (def fullscreen? true)
    (def fullscreen? false))
  (run))

(defn run
  []
  (try
    (init)
    (while (not done?)
      (mainloop)
      (render)
      (Display/update))
    (cleanup)
    (catch Exception e
      (.printStackTrace e)
      (exit-application))))

(defn mainloop
  []
  (when (Keyboard/isKeyDown Keyboard/KEY_ESCAPE)
    (def done? true))
  (when (Display/isCloseRequested)
    (def done? true))
  (when (Keyboard/isKeyDown Keyboard/KEY_F1)
    (if (not f1?)
      (do
        (def f1? true)
        (switch-mode))
      (def f1? false))))

(defn switch-mode
  []
  (def fullscreen? (not fullscreen?))
  (try
    (Display/setFullscreen fullscreen?)
    (catch Exception e
      (.printStackTrace e))))

(defn render
  []
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (GL11/glTranslatef -1.5 0.0 -6.0)  ; Move Left 1.5 Units And Into The Screen 6.0
  (GL11/glRotatef rtri 0.0 1.0 0.0)  ; Rotate The Triangle On The Y axis ( NEW )
  (GL11/glBegin GL11/GL_TRIANGLES)   ; Drawing Using Triangles
  (GL11/glColor3f 1.0 0.0 0.0)       ; Red
  (GL11/glVertex3f 0.0 1.0 0.0)      ; Top Of Triangle (Front)
  (GL11/glColor3f 0.0 1.0 0.0)       ; Green
  (GL11/glVertex3f -1.0 -1.0 1.0)    ; Left Of Triangle (Front)
  (GL11/glColor3f 0.0 0.0 1.0)       ; Blue
  (GL11/glVertex3f 1.0 -1.0 1.0)     ; Right Of Triangle (Front)
  (GL11/glColor3f 1.0 0.0 0.0)       ; Red
  (GL11/glVertex3f 0.0 1.0 0.0)      ; Top Of Triangle (Right)
  (GL11/glColor3f 0.0 0.0 1.0)       ; Blue
  (GL11/glVertex3f 1.0 -1.0 1.0)     ; Left Of Triangle (Right)
  (GL11/glColor3f 0.0 1.0 0.0)       ; Green
  (GL11/glVertex3f 1.0 -1.0 -1.0)    ; Right Of Triangle (Right)
  (GL11/glColor3f 1.0 0.0 0.0)       ; Red
  (GL11/glVertex3f 0.0 1.0 0.0)      ; Top Of Triangle (Back)
  (GL11/glColor3f 0.0 1.0 0.0)       ; Green
  (GL11/glVertex3f 1.0 -1.0 -1.0)    ; Left Of Triangle (Back)
  (GL11/glColor3f 0.0 0.0 1.0)       ; Blue
  (GL11/glVertex3f -1.0 -1.0 -1.0)   ; Right Of Triangle (Back)
  (GL11/glColor3f 1.0 0.0 0.0)       ; Red
  (GL11/glVertex3f 0.0 1.0 0.0)      ; Top Of Triangle (Left)
  (GL11/glColor3f 0.0 0.0 1.0)       ; Blue
  (GL11/glVertex3f -1.0 -1.0 -1.0)   ; Left Of Triangle (Left)
  (GL11/glColor3f 0.0 1.0 0.0)       ; Green
  (GL11/glVertex3f -1.0 -1.0 1.0)    ; Right Of Triangle (Left)
  (GL11/glEnd)                       ; Finished Drawing The Triangle
  
  (GL11/glLoadIdentity)              ; Reset The Current Modelview Matrix
  (GL11/glTranslatef 1.5 0.0 -7.0)   ; Move Right 1.5 Units And Into The Screen 6.0
  (GL11/glRotatef rquad 1.0 1.0 1.0) ; Rotate The Quad On The X axis ( NEW )
  (GL11/glColor3f 0.5 0.5 1.0)       ; Set The Color To Blue One Time Only
  (GL11/glBegin GL11/GL_QUADS)       ; Draw A Quad
  (GL11/glColor3f 0.0 1.0 0.0)       ; Set The Color To Green
  (GL11/glVertex3f 1.0 1.0 -1.0)     ; Top Right Of The Quad (Top)
  (GL11/glVertex3f -1.0 1.0 -1.0)    ; Top Left Of The Quad (Top)
  (GL11/glVertex3f -1.0 1.0 1.0)     ; Bottom Left Of The Quad (Top)
  (GL11/glVertex3f 1.0 1.0 1.0)      ; Bottom Right Of The Quad (Top)
  (GL11/glColor3f 1.0 0.5 0.0)       ; Set The Color To Orange
  (GL11/glVertex3f 1.0 -1.0 1.0)     ; Top Right Of The Quad (Bottom)
  (GL11/glVertex3f -1.0 -1.0 1.0)    ; Top Left Of The Quad (Bottom)
  (GL11/glVertex3f -1.0 -1.0 -1.0)   ; Bottom Left Of The Quad (Bottom)
  (GL11/glVertex3f 1.0 -1.0 -1.0)    ; Bottom Right Of The Quad (Bottom)
  (GL11/glColor3f 1.0 0.0 0.0)       ; Set The Color To Red
  (GL11/glVertex3f 1.0 1.0 1.0)      ; Top Right Of The Quad (Front)
  (GL11/glVertex3f -1.0 1.0 1.0)     ; Top Left Of The Quad (Front)
  (GL11/glVertex3f -1.0 -1.0 1.0)    ; Bottom Left Of The Quad (Front)
  (GL11/glVertex3f 1.0 -1.0 1.0)     ; Bottom Right Of The Quad (Front)
  (GL11/glColor3f 1.0 1.0 0.0)       ; Set The Color To Yellow
  (GL11/glVertex3f  1.0 -1.0 -1.0)   ; Bottom Left Of The Quad (Back)
  (GL11/glVertex3f -1.0 -1.0 -1.0)   ; Bottom Right Of The Quad (Back)
  (GL11/glVertex3f -1.0  1.0 -1.0)   ; Top Right Of The Quad (Back)
  (GL11/glVertex3f 1.0 1.0 -1.0)     ; Top Left Of The Quad (Back)
  (GL11/glColor3f 0.0 0.0 1.0)       ; Set The Color To Blue
  (GL11/glVertex3f -1.0 1.0 1.0)     ; Top Right Of The Quad (Left)
  (GL11/glVertex3f -1.0 1.0 -1.0)    ; Top Left Of The Quad (Left)
  (GL11/glVertex3f -1.0 -1.0 -1.0)   ; Bottom Left Of The Quad (Left)
  (GL11/glVertex3f -1.0 -1.0 1.0)    ; Bottom Right Of The Quad (Left)
  (GL11/glColor3f 1.0 0.0 1.0)       ; Set The Color To Violet
  (GL11/glVertex3f 1.0 1.0 -1.0)     ; Top Right Of The Quad (Right)
  (GL11/glVertex3f 1.0 1.0 1.0)      ; Top Left Of The Quad (Right)
  (GL11/glVertex3f 1.0 -1.0 1.0)     ; Bottom Left Of The Quad (Right)
  (GL11/glVertex3f 1.0 -1.0 -1.0)    ; Bottom Right Of The Quad (Right)
  (GL11/glEnd)                       ; Done Drawing The Quad
  
  (def rtri (+ rtri 0.04))            ; Increase The Rotation Variable For The Triangle
  (def rquad (- rquad 0.03))         ; Decrease The Rotation Variable For The Quad
)

(defn create-window
  []
  (Display/setFullscreen fullscreen?)
  (let [d (Display/getAvailableDisplayModes)]
    (doseq [i d]
      (when (and
              (= screen-w (.getWidth i))
              (= screen-h (.getHeight i))
              (= screen-bpp (.getBitsPerPixel i)))
        (def display-mode i)))
    (when (nil? display-mode)
      (def display-mode (DisplayMode. default-screen-w default-screen-h)))
    (Display/setDisplayMode display-mode)
    (Display/setTitle window-title)
    (Display/create)))

(defn init
  []
  (create-window)
  (init-gl))

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
  ;; Really Nice Perspective Calculations
  (GL11/glHint GL11/GL_PERSPECTIVE_CORRECTION_HINT GL11/GL_NICEST))

(defn cleanup
  []
  (Display/destroy))


(defn exit-application
  []
  (when release?
    (System/exit 0)))




