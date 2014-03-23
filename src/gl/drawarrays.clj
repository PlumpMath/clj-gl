(ns gl.drawarrays
  (:import [org.lwjgl BufferUtils LWJGLException])
  (:import [org.lwjgl.opengl ContextAttribs Display DisplayMode GL11 GL15 GL20 GL30 PixelFormat])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.util.glu GLU]))

(def width 320)
(def height 240)
(def vao-id 0)
(def vbo-id 0)
(def vertex-count 0)

(declare start setup-opengl setup-quad loop-cycle destroy-opengl exit-on-gl-error)

(defn start
  []
  (setup-opengl)
  (setup-quad)
  (while (not (Display/isCloseRequested))
    (loop-cycle)
    (Display/sync 60)
    (Display/update))
  (destroy-opengl))

(defn setup-opengl
  "Setup an OpenGL context with API version 3.2"
  []
  (try
    (Display/create)
    (def pixel-format (PixelFormat.))
    (def context-attributes (ContextAttribs. 3 2))
    (.withForwardCompatible context-attributes true)
    (.withProfileCore context-attributes true)
    (Display/setDisplayMode (DisplayMode. width height))
  (catch LWJGLException e
    (.printStackTrace e)
    (System/exit -1)))
  ; Setup an XNA like background color
  (GL11/glClearColor 0.4 0.6 0.9 0.0)
  ; Map the internal OpenGL coordinate system to the entire screen
  (GL11/glViewport 0 0 width height)
  (exit-on-gl-error "Error in setup-opengl"))

(defn setup-quad
  []
  ; OpenGL expects vertices to be defined counter clockwise by default
  (def vtxs
    (into-array
      Float/TYPE
      [; Left bottom triangle
       -0.5 0.5 0
       -0.5 -0.5 0
       0.5 -0.5 0
       ; Right top triangle
       0.5 -0.5 0
       0.5 0.5 0
       -0.5 0.5 0]))
  ; Sending data to OpenGL requires the usage of (flipped) byte buffers
  (def vtxs-buffer (BufferUtils/createFloatBuffer (count vtxs)))
  (.put vtxs-buffer vtxs)
  (.flip vtxs-buffer)
  (def vtxs-count 6)

  ; Create a new Vertex Array Object in memory and select it (bind)
  ; VAO can have up to 16 attributes (VBO's) assigned to it by default
  (def vao-id (GL30/glGenVertexArrays))
  (GL30/glBindVertexArray vao-id)

  ; Create a new Vertex Buffer Object in memory and select it (bind)
  ; VBO is a collection of Vectors which in this case resemble the location of each vertex.
  (def vbo-id (GL15/glGenBuffers))
  (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER vbo-id)
  (GL15/glBufferData GL15/GL_ARRAY_BUFFER vtxs-buffer GL15/GL_STATIC_DRAW)
  ; Put the VBO in the attributes list at index 0
  (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)
  ; Deselect (bind to 0) the VBO
  (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER 0)
  ; Deselect (bind to 0) the VAO
  (GL30/glBindVertexArray 0)
  (exit-on-gl-error "Error in setup-quad"))

(defn loop-cycle
  []
  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)

  ; Bind to the VAO that has all the information about the quad vertices
  (GL30/glBindVertexArray vao-id)
  (GL20/glEnableVertexAttribArray 0)
  ; Draw the vertices
  (GL11/glDrawArrays GL11/GL_TRIANGLES 0 vtxs-count)
  ; Put everything back to default (deselect)
  (GL20/glDisableVertexAttribArray 0)
  (GL30/glBindVertexArray 0)
  
  (exit-on-gl-error "Error in loop-cycle"))

(defn destroy-opengl
  []
  ; Disable the VBO index from the VAO attributes list
  (GL20/glDisableVertexAttribArray 0)
  
  ; Delete the VBO
  (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER 0)
  (GL15/glDeleteBuffers vbo-id)
  
  ; Delete the VAO
  (GL30/glBindVertexArray 0)
  (GL30/glDeleteVertexArrays vao-id)

  (Display/destroy))

(defn exit-on-gl-error
  [error-message]
  (let [error-value (GL11/glGetError)]
    (when (not= error-value GL11/GL_NO_ERROR)
      (binding [*out* *err*] (println "ERROR -" error-message (GLU/gluErrorString error-value)))
      (when (Display/isCreated)
        (Display/destroy))
      (System/exit -1))))



