(ns so.modernized.colder-wars.math
  (:import [com.badlogic.gdx.math Vector2 Vector3 Quaternion Matrix3 Matrix4]))

(defn make-vector
  ([^Double f1 ^Double f2] (Vector2. (float f1) (float f2)))
  ([^Double f1 ^Double f2 ^Double f3] (Vector3. (float f1) (float f2) (float f3))))

(defn make-quaternion
  [^Double f1 ^Double f2 ^Double f3 ^Double f4] (Quaternion. (float f1) (float f2) (float f3) (float f4)))



(defn make-matrix
  ([[a b c]
    [d e f]
    [g h i]] (Matrix3. (float-array [a b c d e f g h i])))
  ([[a b c d]
    [e f g h]
    [i j k l]
    [m n o p]] (Matrix4. (float-array [a b c d e f g h i j k l m n o p]))))

(make-matrix [1 0 0 0]
             [0 1 0 0]
             [0 0 1 0]
             [0 0 0 1])