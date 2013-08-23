(ns colder-wars.gl-support.core
  (:import
   (java.nio FloatBuffer)
   (org.lwjgl BufferUtils)
   (org.lwjgl.opengl GL11 GL15 GL20 GL30 Display DisplayMode ContextAttribs PixelFormat)
   (org.lwjgl.util.glu GLU)))

(defonce varrays (agent {}))
(defonce vbuffers (agent {}))

(defn- push-array (partial send varrays assoc))
(defn- push-buffer (partial send vbuffers assoc))

(defmacro report-opengl-errors
  [form]
  `(let [result ~form
         error-val (GL11/glGetError)]
     (if (not= GL11/GL_NO_ERROR error-val)
       (throw (OpenGLException. (str "OpenGL reported error:" (GLU/gluErrorString error-val)))))
     result))

(defmacro with-varray
  [varr & body]
  `(let [varr-id (report-opengl-errors (GL30/glGenVertexArrays))]
     (push-array ~varr varr-id)
     (report-opengl-errors (GL30/glBindVertexArray varr-id))
     (do ~@body)
     (report-opengl-errors (GL30/glBindVertexArray 0))))

(defmacro with-vbuffer
  [vbuf buffer-type & body]
  `(let [vbuf-id (report-opengl-errors (GL30/glGenBuffers))]
     (push-buffer ~vbuf vbuf-id)
     (report-opengl-errors (GL30/glBindBuffers buffer-type vbuf-id))
     (do ~@body)
     (report-opengl-errors (GL30/glBindBuffers buffer-type 0))))

(defn ^FloatBuffer float-buffer
  [& flts]
  (let [arr (float-array flts)
        len (count arr)]
    (-> (BufferUtils/createFloatBuffer len)
        (.put arr)
        (.flip))))

