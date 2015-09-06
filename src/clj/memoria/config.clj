(ns memoria.config
  (:require [ragtime.jdbc :as r-jdbc]))

(def dev-db-uri "jdbc:postgresql://localhost:5432/memoria_dev")
(def test-db-uri "jdbc:postgresql://localhost:5432/memoria_test")

(def ragtime-dev-config {:datastore (r-jdbc/sql-database {:connection-uri dev-db-uri})
                         :migrations (r-jdbc/load-resources "migrations")})

(def ragtime-test-config {:datastore (r-jdbc/sql-database {:connection-uri test-db-uri})
                          :migrations (r-jdbc/load-resources "migrations")})

(defn db-map-from-uri [url]
  (let [matches (re-find #"jdbc:postgresql://(.+):(.+)/(.+)" url)]
    {:db (get matches 3)
     :host (get matches 1)
     :port (get matches 2)
     :make-pool? false}))

(def dev-db-map (db-map-from-uri dev-db-uri))
(def test-db-map (db-map-from-uri test-db-uri))
