(require 'leiningen.core.eval)

(def LWJGL-CLASSIFIER
  "Per os native code classifier"
  {:macosx "natives-osx"
   :linux "natives-linux"
   :windows "natives-windows"})

(defn lwjgl-classifier
  "Return the os-dependent lwjgl native-code classifier"
  []
  (let [os (leiningen.core.eval/get-os)]
    (get LWJGL-CLASSIFIER os)))

(defproject colder-wars "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [net.mikera/core.matrix "0.9.0"]
                 [net.mikera/vectorz-clj "0.13.2"]
                 [org.lwjgl.lwjgl/lwjgl "2.9.0"]
                 [org.lwjgl.lwjgl/lwjgl_util "2.9.0"]
                 [org.lwjgl.lwjgl/lwjgl-platform "2.9.0"
                  :classifier ~(lwjgl-classifier)
                  :native-prefix ""]]
  :jvm-opts [~(str "-Djava.library.path=native/:"
                   (System/getProperty "java.library.path"))]
  :main colder-wars.core)
