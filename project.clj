(defproject colder-wars "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.badlogicgames.gdx/gdx "1.0-SNAPSHOT"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.0-SNAPSHOT"]
                 [com.badlogicgames.gdx/gdx-platform "1.0-SNAPSHOT"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "1.0-SNAPSHOT"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "1.0-SNAPSHOT"]]
  :repositories [["sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"}]]
  :global-vars {*warn-on-reflection* true}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :aot [#"so.modernized.colder-wars.*"]
  :main so.modernized.colder-wars.desktop-launcher)
