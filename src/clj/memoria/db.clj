(ns memoria.db
  (:import com.zaxxer.hikari.HikariConfig com.zaxxer.hikari.HikariDataSource)
  (:require [hikari-cp.core :as h]
            [ragtime.jdbc :as r-jdbc]
            [ragtime.repl :as ragtime]))

(def datasource-options {:pool-name     "memoria-pool"
                         :adapter       "postgresql"
                         :username      (System/getenv "MEMORIA_DB_USERNAME")
                         :password      (System/getenv "MEMORIA_DB_PASSWORD")
                         :database-name (or (System/getenv "MEMORIA_DB_NAME") "memoria_dev")
                         :server-name   (or (System/getenv "MEMORIA_DB_HOST") "localhost")
                         :port-number   (or (System/getenv "MEMORIA_PORT") "5432")})

(def test-datasource-options (assoc datasource-options
                                    :pool-name "memoria-test-pool"
                                    :database-name "memoria_test"))

(def ^:dynamic *conn*)

(defn database-uri
  "Returns the JDBC connection URI based on the defined environment variables.
  If the keyword :test is passed as the first argument, then the memoria_test
  database will be used.
  Any arguments different from :test will be ignored."
  [& args]
  (let [test? (= (first args) :test)
        {:keys [server-name port-number]} datasource-options
        database-name (if test? "memoria_test" (:database-name datasource-options))]
    (str "jdbc:postgresql://" server-name ":" port-number "/" database-name)))

(def ^:private ds (delay (h/make-datasource datasource-options)))
(def ^:private test-ds (delay (h/make-datasource test-datasource-options)))

(defn ragtime-config
  "Returns the ragtime configuration map for the given environment.
  When receiving :test as the first argument, the test database
  will be used."
  [& args]
  (let [uri (apply database-uri args)]
    {:datastore (r-jdbc/sql-database {:connection-uri uri})
     :migrations (r-jdbc/load-resources "migrations")}))

(defn migrate-db
  "Migrates the database, applying all pending migrations.
  When receiving :test as the first argument, will migrate the
  test database."
  [& args]
  (ragtime/migrate (apply ragtime-config args)))

(defn rollback-db
  "Rollbacks the last applied database migration.
  When receiving :test as the first argument, will rollback the
  test database."
  [& args]
  (ragtime/rollback (apply ragtime-config args)))

(defn datasource
  "Returns a datasource with the connection pool."
  []
  {:datasource @ds})

(defn test-datasource
  "Returns a datasource with the connection pool for the test database."
  []
  {:datasource @test-ds})

(defn close-datasource
  "Closes the connection pool"
  []
  (if (realized? ds)
    (h/close-datasource @ds)))

