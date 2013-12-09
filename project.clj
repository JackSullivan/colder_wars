(defproject colder-wars "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [so.modernized.high-concept "0.0.1-SNAPSHOT"]
                 [com.badlogicgames.gdx/gdx "0.9.9"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "0.9.9"]
                 [com.badlogicgames.gdx/gdx-platform "0.9.9"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "0.9.9"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "0.9.9"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :aot [#"so.modernized.colder-wars.*"]
  :main so.modernized.colder-wars.desktop-launcher)
