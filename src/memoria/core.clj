(ns memoria.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer  [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [taoensso.timbre :as timbre]
            [korma.db :refer [defdb postgres]]
            [memoria.config :as config]
            [memoria.handlers.cards :as cards-handler]))

(defdb db (postgres config/dev-db-map))

(defroutes app-routes
  cards-handler/cards-routes)

(def app
  (wrap-params (-> app-routes
                   wrap-json-body
                   wrap-json-response)))

