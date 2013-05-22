(ns colder-wars.display
  (:use quil.core
        [colder-wars.physics :only [generate-bodies future-states]]))


(defn draw-body
  [body]
  (let [[x y z] (:location body)
        size (/ (:size body) 2)]
    (ellipse x y size size)))

(defn setup
  []
  (smooth)
  (frame-rate 1)
  (background 20))

;(defn draw
;  []
;  (let [next-state (first @fut)]
;    (send fut rest)
;    (dorun (map draw-body next-state))))

;(sketch :setup setup :draw draw)