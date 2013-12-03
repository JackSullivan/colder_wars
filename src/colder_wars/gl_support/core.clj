(ns colder-wars.gl-support.core
  (:use (colder-wars.gl-support.OpenGLException))
  (:import
   (java.nio FloatBuffer)
   (org.lwjgl BufferUtils)
   (org.lwjgl.opengl GL11 GL15 GL20 GL30 Display DisplayMode ContextAttribs PixelFormat)
   (org.lwjgl.util.glu GLU)))

(defonce varrays (agent {}))
(defonce vbuffers (agent {}))

(def push-array (partial send varrays assoc))
(def push-buffer (partial send vbuffers assoc))

(defn- clear-vbuffers []
  (map #(GL15/glDeleteBuffers %) (vals @vbuffers)))
(defn- clear-varrays []
  (map #(GL30/glDeleteVertexArrays %) (vals @varrays)))

(defn- check-gl-exception []
  (let [error-val (GL11/glGetError)]
    (if (not= GL11/GL_NO_ERROR error-val)
       (throw (Exception. (str "OpenGL reported error:" (GLU/gluErrorString error-val)))))))

(defn- prepare-display
  ([title]
   (prepare-display [800 600] (-> (ContextAttribs. 3 2)
                                (.withProfileCore true)
                                (.withForwardCompatible true)) (PixelFormat.) title))
  ([[width height] ^ContextAttribs context ^PixelFormat pixel-format title]
   (Display/setDisplayMode (DisplayMode. width height))
   (Display/create pixel-format context)
   (Display/setTitle title)
   (GL11/glViewport 0 0 width height)))

(defmacro report-opengl-errors
  [form]
  `(let [result# ~form]
     (check-gl-exception)
     result#))

(defmacro with-gl-exceptions
  [& body]
  `(do ~@(interleave body (repeat `(check-gl-exception)))))

(defmacro with-varray ;; TODO add ability to retrieve stored
  [varr & body]
  `(let [varr-id# (report-opengl-errors (GL30/glGenVertexArrays))]
     (push-array ~varr varr-id#)
     (report-opengl-errors (GL30/glBindVertexArray varr-id#))
     (do ~@body)
     (report-opengl-errors (GL30/glBindVertexArray 0))))

(defmacro with-vbuffer ;; TODO Add ability to retrieve stored
  [vbuf buffer-type & body]
  `(let [vbuf-id# (report-opengl-errors (GL15/glGenBuffers))]
     (push-buffer ~vbuf vbuf-id#)
     (report-opengl-errors (GL15/glBindBuffer ~buffer-type vbuf-id#))
     (do ~@body)
     (report-opengl-errors (GL15/glBindBuffer ~buffer-type 0))))

(defmacro with-opengl-context
  [title & body]
  `(do (prepare-display ~title)
     ~@body
     (clear-vbuffers)
     (clear-varrays)
     (Display/destroy))) ;; Other cleanup actions here

(defn ^FloatBuffer float-buffer
  [& flts]
  (let [arr (float-array flts)
        len (count arr)]
    (-> (BufferUtils/createFloatBuffer len)
        (.put arr)
        (.flip))))

(with-opengl-context "VAO High-Level Test"
  (GL11/glClearColor (float 0.4) (float 0.6) (float 0.9) (float 0))
  (with-varray :arr
    (with-vbuffer :buf GL15/GL_ARRAY_BUFFER
      (GL15/glBufferData GL15/GL_ARRAY_BUFFER
                     (float-buffer
                      -0.5 0.5 0.0 ; left bottom triangle
                      -0.5 -0.5 0.0
                      0.5 -0.5 0.0
                      0.5 -0.5 0.0 ; right top triangle
                      0.5 0.5 0.0
                      -0.5 0.5 0.0)
                     GL15/GL_STATIC_DRAW)
      ))
  (while (not (Display/isCloseRequested))
    (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
    (Display/update)
    (Display/sync 60)))
;;   (with-varray
;;     (with-vbuffer))))

;; (Display/destroy)