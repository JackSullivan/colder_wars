(ns colder-wars.models
  (:use clojure.core.matrix)
  (:use clojure.core.matrix.operators))

(defrecord Body [mass position velocity acceleration shape])

(defn- apply-to
  [factor base application]
  (+ (* factor application)
     base))

;(def b (Body. 1000 (matrix [0 0 0]) (matrix [1 0 0]) (matrix [-0.25 0.25 0]) nil))

(defn- update-coord
  [applicand applicator timestep body]
  (assoc body applicand
    (apply-to timestep (applicand body) (applicator body))))

(def update-position (partial update-coord :position :velocity))
(def update-velocity (partial update-coord :velocity :acceleration))

(defn advance-time
  [timestep body]
  (-> body (partial update-position timestep) (partial update-velocity timestep)))