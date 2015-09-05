(ns memoria.handlers.app
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer  [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [taoensso.timbre :as timbre]
            [memoria.db :as db]
            [memoria.handlers.cards :as cards-handler]))

(defn wrap-request-logging [app]
  (fn [request]
    (timbre/info (str "Received request: " request))
    (let [response (app request)]
      (timbre/info (str "Response: " response "\n"))
      response)))

(defn wrap-db-conn [app]
  (fn [request]
    (let [datasource (if (= "test" (get-in request [:headers "memoria-mode"]))
                       (db/test-datasource)
                       (db/datasource))]
      (binding [db/*conn* datasource]
        (app request)))))

(defroutes app-routes
  cards-handler/cards-routes)

(def app
  (-> app-routes
      wrap-params
      wrap-json-body
      wrap-json-response
      wrap-db-conn
      wrap-request-logging))

