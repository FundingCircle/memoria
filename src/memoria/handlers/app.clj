(ns memoria.handlers.app
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer  [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [taoensso.timbre :as timbre]
            [memoria.handlers.cards :as cards-handler]))

(defn wrap-request-logging [app]
  (fn [request]
    (timbre/info (str "Received request: " request))
    (let [response (app request)]
      (timbre/info (str "Response: " response "\n"))
      response)))

(defroutes app-routes
  cards-handler/cards-routes)

(def app
  (-> app-routes
      wrap-params
      wrap-json-body
      wrap-json-response
      wrap-request-logging))

