(ns so.modernized.colder-wars.math
  (:import [com.badlogic.gdx.math Vector2 Vector3 Quaternion Matrix3 Matrix4]
           [com.badlogic.gdx.physics.bullet.linearmath btTransform]))


;TODO
; - wrappers for matrix, vector, quaternion, and transform, conversion between transform and matrix4
; - vector/matrix pool?

(defprotocol MatrixLike
  (dot! [this other])
  (invert! [this])
  (transpose! [this])
  (set! [this values])
  (determinant [this]))


(defn make-vector
  ([^double f1 ^double f2] (Vector2. f1 f2))
  ([^double f1 ^double f2 ^double f3] (Vector3. f1 f2 f3)))

(defn make-quaternion
  [^double f1 ^double f2 ^double f3 ^double f4] (Quaternion. f1 f2 f3 f4))

(defn eye
  ([] (eye 3))
  ([size]
  (cond (= 3 size) (Matrix3.)
        (= 4 size) (Matrix4.)
        :else (throw (IllegalArgumentException. "Only matrices of size 3 and 4 are supported")))))

(defn empty-vec
  ([] (empty-vec 3))
  ([size]
   (cond (= 3 size) (Vector3.)
         (= 2 size) (Vector2.)
         :else (throw (IllegalArgumentException. "Only vectors of size 2 and 3 are supported")))))

(defn make-matrix
  ([^floats fs]
   (let [len-fs (alength fs)]
     (cond (>= len-fs 16) (Matrix4. fs)
           (>= len-fs 9)  (Matrix3. fs)
           :else (throw (IllegalArgumentException. (str "Array must have at least 9 elements (found" len-fs ")"))))))
;  ([^btTransform t] (Matrix4. t))
  ([[^double a ^double b ^double c]
    [^double d ^double e ^double f]
    [^double g ^double h ^double i]] (Matrix3. (float-array [a b c d e f g h i])))
  ([[^double a ^double b ^double c ^double d]
    [^double e ^double f ^double g ^double h]
    [^double i ^double j ^double k ^double l]
    [^double m ^double n ^double o ^double p]] (Matrix4. (float-array [a b c d e f g h i j k l m n o p]))))

