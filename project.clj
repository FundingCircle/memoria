(defproject memoria "0.1.0-SNAPSHOT"
  :description "A webapp to save things that you would otherwise forget."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.3"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [reagent "0.5.1-rc3"]
                 [cljs-ajax "0.3.14"]
                 [yesql "0.5.0-rc3"]
                 [hikari-cp "1.2.4"]
                 [clj-time "0.11.0"]
                 [com.andrewmcveigh/cljs-time "0.3.13"]
                 [ragtime "0.5.2"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [compojure "1.4.0"]
                 [ring "1.4.0"]
                 [ring/ring-json "0.3.1"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.0.0"]
                 [com.taoensso/timbre "4.0.2"]
                 [slingshot "0.12.2"]
                 [bouncer "0.3.3"]
                 [selmer "0.9.1"]
                 [prismatic/dommy "1.1.0"]]

  :plugins [[lein-ring "0.9.6"]
            [lein-environ "1.0.0"]
            [lein-figwheel "0.3.9"]
            [lein-cljsbuild  "1.1.0"]]

  :clean-targets [:target-path "resources/public/js/out"]
  :source-paths ["src/clj" "src/cljs"]
  :repl-options {:init (require '[memoria.repl :refer :all])
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[ring/ring-mock "0.2.0"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [com.cemerick/austin "0.1.6"]
                                  [org.clojure/tools.nrepl "0.2.10"]]}}

  :cljsbuild {:builds [{:id "production"
                        :source-paths ["src/cljs/"]
                        :compiler {:output-to "resources/public/js/memoria.js"
                                   :output-dir "resources/public/js/out"
                                   :main "memoria.app"
                                   :asset-path "js/out"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       {:id "dev"
                        :source-paths ["src/cljs/" "test"]
                        :figwheel {:on-jsload "memoria.test/run"}
                        :compiler {:output-to "resources/public/js/memoria.js"
                                   :output-dir "resources/public/js/out"
                                   :main "memoria.app"
                                   :asset-path "js/out"
                                   :optimizations :none}}

                       {:id "test"
                        :source-paths ["src/cljs/" "test"]
                        :compiler {:output-to "resources/test/compiled.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]
              :test-commands {"test" ["phantomjs"
                                      "resources/test/test.js"
                                      "resources/test/test.html"]}}

  :figwheel {:css-dirs ["resources/public/css"]}

  :ring  {:handler memoria.core/app})
