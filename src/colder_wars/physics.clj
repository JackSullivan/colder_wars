
(ns colder-wars.physics
  (:use [incanter.core :only [$= matrix plus abs matrix-map view]]
        [incanter.stats :only [sample-uniform euclidean-distance]]
        [incanter.charts :only [scatter-plot add-lines]]
        [clojure.math.combinatorics :only [cartesian-product]]))

(def G 6.674E-11)
(defrecord Body-rec [location velocity mass shape])

(def zip (partial map vector))
(def unzip (partial apply map vector))

(defn body
  [location velocity mass shape]
  (Body-rec. location velocity mass shape))

(defn apply-velocity
  "
    Returns a new body with its current velocity applied over the given
    timestep.
  "
  [timestep {:keys [location velocity] :as body}]
  (merge body {:location ($= location + velocity * timestep)}))

(defn apply-acceleration
  "
    Returns a new body with the supplied acceleration applied over the given
    timestep.
  "
  [timestep {:keys [velocity] :as body} acceleration]
  (merge body {:velocity ($= velocity + acceleration * timestep)}))

(defn gravity
  "
    Returns the acceleration vector that one body exerts on another due to
    gravity.
  "
  [{loc-1 :location m-1 :mass} {loc-2 :location}]
  (let [distance ($= loc-1 - loc-2)]
    (matrix (matrix-map #(if (= Infinity %) 0 (- %)) ($= G * m-1 / (distance ** 2))))))

(defn apply-gravity
  "
    Applies the accelerations due to gravitational force of each body on each
    other body in the system over the given timestep, returning the changed
    bodies.
  "
  [timestep bodies]
  (let [pairs (filter (partial apply not=) (cartesian-product bodies bodies))
        gravity-step (fn [body-1 body-2]
                       {body-2 (gravity body-1 body-2)})
        gravity-steps (apply merge-with plus (map (partial apply gravity-step) pairs))]
    (map (partial apply (partial apply-acceleration timestep)) gravity-steps)))

(defn advance-time
  [timestep bodies]
  (map (partial apply-velocity timestep) (apply-gravity timestep bodies)))

(defn future-states
  [stepsize bodies]
  (let [step (partial advance-time stepsize)]
    (iterate step bodies)))


(defn generate-bodies
  [num-bodies]
  (let [sample-coords (fn [] (matrix (sample-uniform 3 :min 1 :max 100)))
        sample-scalar (fn [] (first (sample-uniform 1 :min 1000 :max 50000)))
        generate-body (fn [] (Body-rec. (sample-coords) (sample-coords) (sample-scalar) (sample-scalar)))]
    (repeatedly num-bodies generate-body)))

(def earth (body (matrix [0 0 0]) (matrix [0 0 0]) 5.9E24 6300))

(def moon (body (matrix [384000 0 0]) (matrix [0 0 0]) 7.3E22 3400))

;(advance-time 1 [moon earth])

;(view (doto (scatter-plot (range 10) distances)
;  (add-lines (range 10) distances)))