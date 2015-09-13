(ns memoria.handlers.app
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer  [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring-gatekeeper.authenticators.auth0 :as auth0]
            [ring-gatekeeper.core :as auth]
            [taoensso.timbre :as timbre]
            [selmer.parser :as selmer]
            [memoria.db :as db]
            [memoria.handlers.cards :as cards-handler]
            [memoria.entities.cards :as cards]))

(selmer.parser/set-resource-path! (clojure.java.io/resource "public"))

(def ^:private auth0-authenticator (auth0/new-authenticator {:can-handle-request-fn (constantly true)
                                                             :client-id (System/getenv "MEMORIA_AUTH0_CLIENT_ID")
                                                             :client-secret (System/getenv "MEMORIA_AUTH0_CLIENT_SECRET")
                                                             :subdomain (System/getenv "MEMORIA_AUTH0_SUBDOMAIN")}))

(defn wrap-request-auth
  "Checks if the user is allowed to access the application using Auth0"
  [app]
  (auth/authenticate app [auth0-authenticator]))

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
      wrap-request-auth
      wrap-params
      wrap-json-body
      wrap-json-response
      wrap-db-conn
      wrap-request-logging))

