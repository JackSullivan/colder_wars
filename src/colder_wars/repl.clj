;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.

(use 'colder-wars.physics
     '[incanter.charts :only [scatter-plot add-lines]]
     '[incanter.stats :only [euclidean-distance]])

(unzip (map (partial map (comp first :location)) (take 10 (future-states 1 [moon earth]))))

(view
 (let [[moon-loc earth-loc] (unzip (map (partial map (comp first :location)) (take 10 (future-states 1 [moon earth]))))
      distances (map (comp abs -) moon-loc earth-loc)]
  (doto (scatter-plot (range 10) distances)
    (add-lines (range 10) moon-loc :series-label "moon")
    (add-lines (range 10) earth-loc :series-label "earth"))))
