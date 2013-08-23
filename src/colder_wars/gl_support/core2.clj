(ns colder-wars.gl-support.core2
  (:import
   (java.nio FloatBuffer)
   (org.lwjgl BufferUtils)
   (org.lwjgl.opengl GL11 GL15 GL20 GL30 Display DisplayMode ContextAttribs PixelFormat)
   (org.lwjgl.util.glu GLU)))

(def varrays (agent []))
(def vbuffers (agent []))

(def add-vbuffer (partial send vbuffers conj))
(def add-varray (partial send varrays conj))

(defn prepare-display
  ([title]
   (prepare-display [800 600] (doto (ContextAttribs. 3 2)
                                (.withProfileCore true)
                                (.withForwardCompatible true)) (PixelFormat.) title))
  ([[width height] ^ContextAttribs context ^PixelFormat pixel-format title]
   (Display/setDisplayMode (DisplayMode. width height))
   (Display/create pixel-format context)
   (Display/setTitle title)
   (GL11/glViewport 0 0 width height)))

(defn ^FloatBuffer float-buffer
  [& flts]
  (let [arr (float-array flts)
        len (count arr)]
    (-> (BufferUtils/createFloatBuffer len)
        (.put arr)
        (.flip))))
;;     (.flip (.put (BufferUtils/createFloatBuffer len) arr))))
;;       (.put arr)
;;       (.flip))))

(defmacro with-varray
  [& body]
  (let [vao-id (GL30/glGenVertexArrays)]
    (GL30/glBindVertexArray vao-id)
    `(do ~@body)
    (GL30/glBindVertexArray 0)
    (add-varray vao-id)))

(defmacro with-vbuffer
  [buffer-type & body]
  (let [vbo-id (GL15/glGenBuffers)]
    (GL15/glBindBuffer buffer-type vbo-id)
    `(do ~@body)
    (GL15/glBindBuffer buffer-type 0)
    (add-vbuffer vbo-id)))

(defmacro with-display
  [title prep & body]
  (prepare-display title)
  `(do ~prep)
  (while (not (Display/isCloseRequested))
    `(do ~@body)
    (Display/sync 60)
    (Display/update))
  (Display/destroy))

(defn- print-error
  [error-text]
  (let [error-val (GL11/glGetError)]
  (if (not= error-val GL11/GL_NO_ERROR)
    (println "ERROR:" error-text ":" (GLU/gluErrorString error-val)))))

(defn- print-shader-error
  [shader-str shader-id]
  (if (= GL11/GL_FALSE (GL20/glGetShaderi shader-id GL20/GL_COMPILE_STATUS))
    (println "ERROR IN SHADER:" shader-str "\nERROR MESSAGE:" (GL20/glGetShaderInfoLog
                                                               shader-id
                                                               (GL20/glGetShaderi shader-id GL20/GL_INFO_LOG_LENGTH)))))

(defn- print-program-error
  [prog-id]
  (if (= GL11/GL_FALSE (GL20/glGetProgrami prog-id GL20/GL_LINK_STATUS))
    (println "ERROR IN PROGRAM:" (GL20/glGetProgramInfoLog
                                  prog-id
                                  (GL20/glGetProgrami prog-id GL20/GL_INFO_LOG_LENGTH)))))

(defmacro with-error-reporting
  [error-string & body]
  `(let [result (do ~@body)]
    (print-error ~error-string)
    result))

(def v-shader-2 "#version 150
  in vec3 vertexPosition_modelspace;

  void main(void) {
    gl_Position.xyz = vertexPosition_modelspace;
    gl_Position.w = 1.0;
  }")

(def f-shader-2 "#version 150
  out vec3 color;

  void main(void) {
    color = vec3(0.5,0.5,0);
  }")

(defn load-shader
  [^String shader-str shader-type]
  (let [shader-id (GL20/glCreateShader shader-type)]
    (GL20/glShaderSource shader-id shader-str)
    (print-error "init-shaders glShaderSource errors?")
    (GL20/glCompileShader shader-id)
    (print-error "init-shaders glCompileShader errors?")
    (print-shader-error shader-str shader-id)
    shader-id))

(Display/setDisplayMode (DisplayMode. 800 600))
(Display/create (PixelFormat. ) (-> (ContextAttribs. 3 2)
                                    (.withProfileCore true)
                                    (.withForwardCompatible true)))
(Display/setTitle "VAO Quad Test")
(GL11/glViewport 0 0 800 600)
(GL11/glClearColor (float 0.4) (float 0.6) (float 0.9) (float 0))
(let [vao-id (with-error-reporting "Vertex Generation" (GL30/glGenVertexArrays))
      vbo-id (with-error-reporting "Buffer Generation" (GL15/glGenBuffers))
      vs-id (load-shader v-shader-2 GL20/GL_VERTEX_SHADER)
      fs-id (load-shader f-shader-2 GL20/GL_FRAGMENT_SHADER)
      prog-id (with-error-reporting "Program Creation" (GL20/glCreateProgram))]
  (GL30/glBindVertexArray vao-id)
  (print-error "Array Binding Error")
  (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER vbo-id)
  (print-error "Buffer Binding Error")
  (GL15/glBufferData GL15/GL_ARRAY_BUFFER
                     (float-buffer
                      -0.5 0.5 0.0 ; left bottom triangle
                      -0.5 -0.5 0.0
                      0.5 -0.5 0.0
                      0.5 -0.5 0.0 ; right top triangle
                      0.5 0.5 0.0
                      -0.5 0.5 0.0)
                     GL15/GL_STATIC_DRAW)
  (print-error "Buffer Data Load Error")
;;   (GL30/glBindVertexArray 0)
;;   (print-error "Vertex unbind")
  (GL20/glAttachShader prog-id vs-id)
  (GL20/glAttachShader prog-id fs-id)
  (GL20/glLinkProgram prog-id)
  (print-program-error prog-id)
;;   (-> prog-id (GL20/glAttachShader vs-id) (GL20/glAttachShader fs-id) GL20/glLinkProgram)
  (print-error "Shader Setup")
  (while (not (Display/isCloseRequested))
    (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
    (GL20/glUseProgram prog-id)
    (print-error "Use Program")
;;     (GL20/glUniform1f angle-loc 0.0)
    (print-error "Uniform")
;;     (GL30/glBindVertexArray vao-id)
    (print-error "Binding Error")
    (GL30/glBindVertexArray vao-id)
    (print-error "Array Binding Error")
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER vbo-id)
    (print-error "Buffer Binding Error")
    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)
    (print-error "Vertex Pointer Error")
    (GL20/glEnableVertexAttribArray 0)
;;     (GL20/glEnableVertexAttribArray 1)
    (print-error "VertexAttribArray")
;;     (with-error-reporting "Drawing Error"
;;       (GL11/glDrawArrays GL11/GL_TRIANGLES 0 3))
    (let [res (GL11/glDrawArrays GL11/GL_TRIANGLES 0 6)]
      (print-error "Drawing Error")
      res)
;;     (print-error "Drawing Error")
    (GL20/glDisableVertexAttribArray 0)
;;     (GL20/glDisableVertexAttribArray 1)
    (print-error "Disable Error")
    (GL30/glBindVertexArray 0)
    (print-error "Unbind Error")
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER 0)
    (print-error "Buffer Unbind")
    (GL20/glUseProgram 0)
    (print-error "unuse program error")
    (Display/sync 60)
    (Display/update)))
(Display/destroy)

;; (prepare-display "VAO Quad Test")
;; (GL11/glClearColor (float 0.4) (float 0.6) (float 0.9) (float 0))
;; (with-varray
;;   (with-vbuffer GL15/GL_ARRAY_BUFFER
;;     (GL15/glBufferData GL15/GL_ARRAY_BUFFER
;;                        verts
;;                        GL15/GL_STATIC_DRAW)
;;     (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)))
;; (while (not (Display/isCloseRequested))
;;   (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
;;   (GL20/glEnableVertexAttribArray 0)
;;   (GL11/glDrawArrays GL11/GL_TRIANGES 0 6)
;;   (GL20/glDisableVertexAttribArray 0)
;;   (Display/sync 60)
;;   (Display/update))
;; (Display/destroy)


;; (with-display "VAO Quad Test"
;;   (do (with-varray
;;         (with-vbuffer GL15/GL_ARRAY_BUFFER
;;           (GL15/glBufferData GL15/GL_ARRAY_BUFFER
;;                              verts
;;                              GL15/GL_STATIC_DRAW)
;;           (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)))
;;     (GL11/glClearColor (float 0.4) (float 0.6) (float 0.9) (float 0)))
;;   (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
;;   (GL20/glEnableVertexAttribArray 0)
;;   (GL11/glDrawArrays GL11/GL_TRIANGES 0 6)
;;   (GL20/glDisableVertexAttribArray 0))