(ns colder-wars.core
  (:gen-class)
  (:import (org.lwjgl.opengl Display ContextAttribs PixelFormat DisplayMode)))

(def UNIVERSAL_TIMESTEP 0.01)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!")
  (let [pixel-format (PixelFormat.)
        context-attribs (-> (ContextAttribs. 3 2)
                            (.withForwardCompatible true)
                            (.withProfileCore true))]
    (Display/setDisplayMode (DisplayMode. 200 300))
    (Display/setTitle "Test Window")
    (Display/create pixel-format context-attribs)))
