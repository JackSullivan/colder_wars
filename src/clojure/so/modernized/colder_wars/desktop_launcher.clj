(ns so.modernized.colder-wars.desktop-launcher
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [so.modernized.colder-wars.core Game])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. (Game.) "colder wars" 800 600 true))
