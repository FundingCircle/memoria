(ns memoria.core
  (:require [korma.db :refer [defdb postgres]]
            [memoria.config :as config]
            [memoria.handlers.app :as app-handlers]))

(defdb db (postgres config/dev-db-map))

(def app app-handlers/app)

