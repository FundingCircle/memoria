(ns memoria.handlers.app
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer  [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [taoensso.timbre :as timbre]
            [selmer.parser :as selmer]
            [memoria.db :as db]
            [memoria.handlers.cards :as cards-handler]
            [memoria.entities.cards :as cards]
            [memoria.tags]))

(selmer.parser/set-resource-path! (clojure.java.io/resource "public"))

(defn wrap-request-logging
  "Logs the data received in the request and the data generated in the response. Useful
  for debugging."
  [app]
  (fn [request]
    (timbre/info (str "Received request: " request))
    (let [response (app request)]
      (timbre/info (str "Response: " response "\n"))
      response)))

(defn wrap-db-conn
  "Gets a new connection from the database connection pool and binds that to the
  memoria.db/*conn* dynamic var.

  If the `memoria-mode` header is present in the request and its value is `test`,
  then the test connection pool will be used. Otherwise, the default pool is used."
  [app]
  (fn [request]
    (let [datasource (if (= "test" (get-in request [:headers "memoria-mode"]))
                       (db/test-datasource)
                       (db/datasource))]
      (binding [db/*conn* datasource]
        (app request)))))

(defroutes page-routes
  (GET "/" req (selmer.parser/render-file "index.html" {:cards (cards/latest db/*conn*)})))

(defroutes app-routes
  (route/resources "/")
  page-routes
  cards-handler/cards-routes)

(def app
  (-> app-routes
      wrap-params
      wrap-json-body
      wrap-json-response
      wrap-db-conn
      wrap-request-logging))

