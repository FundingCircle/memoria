(defproject memoria "0.1.0-SNAPSHOT"
  :description "A webapp to save things that you would otherwise forget."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [korma  "0.4.2"]
                 [ragtime  "0.5.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [compojure  "1.4.0"]
                 [ring  "1.4.0"]
                 [liberator  "0.13"]
                 [com.taoensso/timbre  "4.0.2"]]
  :plugins  [[lein-ring  "0.9.6"]
             [lein-environ  "1.0.0"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :ring  {:handler memoria.core/app})
