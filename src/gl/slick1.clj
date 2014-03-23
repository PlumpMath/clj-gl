(ns gl.slick1
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.newdawn.slick Color])
  (:import [org.newdawn.slick.opengl Texture TextureLoader])
  (:import [org.newdawn.slick.util ResourceLoader]))


(declare start init-gl init render)
(declare texture)

(defn close
  []
  ;(shutdown-agents)
  ;(System/exit 0)
  )


(defn start
  []
  (init-gl 800 600)
  (init)

  (while (not (Display/isCloseRequested))
    (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
    (render)
    (Display/update)
    (Display/sync 60))

  (Display/destroy)
  (close))

(defn init-gl
  [width height]
  (try
    (Display/setDisplayMode (DisplayMode. width height))
    (Display/create)
    (Display/setVSyncEnabled true)
    (catch LWJGLException e
      (.printStackTrace e)
      (System/exit 0)))
  (GL11/glEnable GL11/GL_TEXTURE_2D)
  (GL11/glClearColor 0.0 0.0 0.0 0.0)
  
  ;; enable alpha blending
  (GL11/glEnable GL11/GL_BLEND)
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
  (GL11/glViewport 0 0 width height)
  
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0 width height 0 1 -1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

(defn init
  []
  (try
    ;; load texture from PNG file
    (def texture (TextureLoader/getTexture "PNG" (ResourceLoader/getResourceAsStream "img/test.png")))
    (println "Texture loaded: " texture)
    (println ">> Image width: " (.getImageWidth texture))
    (println ">> Image height: " (.getImageHeight texture))
    (println ">> Texture width: " (.getTextureWidth texture))
    (println ">> Texture height: " (.getTextureHeight texture))
    (println ">> Texture ID: " (.getTextureID texture))
    (catch java.io.IOException e
      (.printStackTrace e))))

(defn render
  []
  (.bind Color/white)
  (.bind texture)
  (GL11/glBegin (GL11/GL_QUADS))
  (GL11/glTexCoord2f 0 0)
  (GL11/glVertex2f 100 100)
  (GL11/glTexCoord2f 1 0)
  (GL11/glVertex2f (+ 100 (.getTextureWidth texture)) 100)
  (GL11/glTexCoord2f 1 1)
  (GL11/glVertex2f (+ 100 (.getTextureWidth texture)) (+ 100 (.getTextureHeight texture)))
  (GL11/glTexCoord2f 0 1)
  (GL11/glVertex2f 100 (+ 100 (.getTextureHeight texture)))
  (GL11/glEnd))

(defn -main
  [& args]
  (start))




