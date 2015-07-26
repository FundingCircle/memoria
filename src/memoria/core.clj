(ns memoria.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]))

(defn my-handler [req]
  (timbre/debug "Received request")
  "<h1>Hello World!</h1>")

(defroutes app
  (GET "/" [] my-handler)
  (route/not-found "<h1>Page not found</h1>"))
