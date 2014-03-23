(ns gl.nehe6
  (:import [java.nio ByteBuffer ByteOrder IntBuffer])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU])
  (:import [org.newdawn.slick Color])
  (:import [org.newdawn.slick.opengl Texture TextureLoader])
  (:import [org.newdawn.slick.util ResourceLoader])
  (:gen-class
    :name gl.nehe6
    :methods [#^{:static true} [start [] Void]]))

(def application-on? true)
(def window-title "NeHe's OpenGL Lesson 6 for LWJGL (Texture Mapping)")
(def fullscreen? false)
(def display-mode nil)
(def default-screen-w 640)
(def default-screen-h 480)
(def default-screen-bpp 24)
(def screen-w 800)
(def screen-h 600)
(def screen-bpp 24)

(def x-rot 0)
(def y-rot 0)
(def z-rot 0)
(def texture nil)

(declare run main-loop switch-mode render create-window
         process-key-event
         init load-textures init-gl clean-up)

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

(defn process-key-event
  []
  (while (Keyboard/next)
    (let [key-state (Keyboard/getEventKey)]
      (when (Keyboard/getEventKeyState)
        (condp = key-state
          Keyboard/KEY_F1 (switch-mode)
          Keyboard/KEY_ESCAPE (def application-on? false)
          nil)))))

(defn main-loop
  []
  (process-key-event)
  (when (Display/isCloseRequested)
    (def application-on? false)))

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
  (GL11/glLoadIdentity)              ; Reset The Current Modelview Matrix
  (GL11/glTranslatef 0.0 0.0 -5.0)   ; Move Right 1.5 Units And Into The Screen 6.0
  (GL11/glRotatef x-rot 1.0 0.0 0.0) ; Rotate On The X Axis
  (GL11/glRotatef y-rot 0.0 1.0 0.0) ; Rotate On The Y Axis
  (GL11/glRotatef z-rot 0.0 0.0 1.0) ; Rotate On The Z Axis
  (.bind Color/white)
  (.bind texture)
  (GL11/glBegin GL11/GL_QUADS)
  ;; Front Face
  (GL11/glTexCoord2f 0.0 0.0)
  (GL11/glVertex3f -1.0 -1.0 1.0) ; Bottom Left Of The Texture and Quad
  (GL11/glTexCoord2f 1.0 0.0)
  (GL11/glVertex3f 1.0 -1.0 1.0) ; Bottom Right Of The Texture and Quad
  (GL11/glTexCoord2f 1.0 1.0)
  (GL11/glVertex3f 1.0 1.0 1.0) ;  Top Right Of The Texture and Quad
  (GL11/glTexCoord2f 0.0 1.0)
  (GL11/glVertex3f -1.0 1.0 1.0) ; Top Left Of The Texture and Quad
  ;; Back Face
  (GL11/glTexCoord2f 1.0 0.0)
  (GL11/glVertex3f -1.0 -1.0 -1.0) ; Bottom Right Of The Texture and Quad
  (GL11/glTexCoord2f 1.0 1.0)
  (GL11/glVertex3f -1.0 1.0 -1.0) ; Top Right Of The Texture and Quad
  (GL11/glTexCoord2f 0.0 1.0)
  (GL11/glVertex3f 1.0 1.0 -1.0) ; Top Left Of The Texture and Quad
  (GL11/glTexCoord2f 0.0 0.0)
  (GL11/glVertex3f 1.0 -1.0 -1.0) ; Bottom Left Of The Texture and Quad
  ;; Top Face
  (GL11/glTexCoord2f 0.0 1.0)
  (GL11/glVertex3f -1.0 1.0 -1.0) ; Top Left Of The Texture and Quad
  (GL11/glTexCoord2f 0.0 0.0)
  (GL11/glVertex3f -1.0 1.0 1.0) ; Bottom Left Of The Texture and Quad
  (GL11/glTexCoord2f 1.0 0.0)
  (GL11/glVertex3f 1.0 1.0 1.0) ; Bottom Right Of The Texture and Quad
  (GL11/glTexCoord2f 1.0 1.0)
  (GL11/glVertex3f 1.0 1.0 -1.0) ; Top Right Of The Texture and Quad
  ;; Bottom Face
  (GL11/glTexCoord2f 1.0 1.0)
  (GL11/glVertex3f -1.0 -1.0 -1.0) ; Top Right Of The Texture and Quad
  (GL11/glTexCoord2f 0.0 1.0)
  (GL11/glVertex3f 1.0 -1.0 -1.0) ; Top Left Of The Texture and Quad
  (GL11/glTexCoord2f 0.0 0.0)
  (GL11/glVertex3f 1.0 -1.0 1.0) ; Bottom Left Of The Texture and Quad
  (GL11/glTexCoord2f 1.0 0.0)
  (GL11/glVertex3f -1.0 -1.0 1.0) ; Bottom Right Of The Texture and Quad
  ;; Right face
  (GL11/glTexCoord2f 1.0 0.0)
  (GL11/glVertex3f 1.0 -1.0 -1.0) ; Bottom Right Of The Texture and Quad
  (GL11/glTexCoord2f 1.0 1.0)
  (GL11/glVertex3f 1.0 1.0 -1.0) ; Top Right Of The Texture and Quad
  (GL11/glTexCoord2f 0.0 1.0)
  (GL11/glVertex3f 1.0 1.0 1.0) ; Top Left Of The Texture and Quad
  (GL11/glTexCoord2f 0.0 0.0)
  (GL11/glVertex3f 1.0 -1.0 1.0) ; Bottom Left Of The Texture and Quad
  ;; Left Face
  (GL11/glTexCoord2f 0.4 0.4)
  (GL11/glVertex3f -1.0 -1.0 -1.0) ; Bottom Left Of The Texture and Quad
  (GL11/glTexCoord2f 0.7 0.4)
  (GL11/glVertex3f -1.0 -1.0 1.0) ; Bottom Right Of The Texture and Quad
  (GL11/glTexCoord2f 0.7 0.7)
  (GL11/glVertex3f -1.0 1.0 1.0) ; Top Right Of The Texture and Quad
  (GL11/glTexCoord2f 0.4 0.7)
  (GL11/glVertex3f -1.0 1.0 -1.0) ; Top Left Of The Texture and Quad
  (GL11/glEnd)
  (def x-rot (+ x-rot 0.03)) ; X Axis Rotation
  (def y-rot (+ y-rot 0.02)) ; Y Axis Rotation
  (def z-rot (+ z-rot 0.04)) ; Z Axis Rotation
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
  (load-textures)
  (init-gl))

(defn load-textures
  []
  (try
    (def texture (TextureLoader/getTexture "PNG" (ResourceLoader/getResourceAsStream "img/seal.png")))
    (println "Texture loaded: " texture)
    (println ">> Image width: " (.getImageWidth texture))
    (println ">> Image height: " (.getImageHeight texture))
    (println ">> Texture width: " (.getTextureWidth texture))
    (println ">> Texture height: " (.getTextureHeight texture))
    (println ">> Texture ID: " (.getTextureID texture))
    (catch java.io.IOException e
      (.printStackTrace e))))
 
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

(defn clean-up
  []
  (Display/destroy))

