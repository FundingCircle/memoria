(ns memoria.support.db-test-helpers
  (:require [clojure.test :refer :all]
            [yesql.core :refer [defqueries]]
            [memoria.db :as db]
            [clojure.java.jdbc :as jdbc]))

(defqueries "sql/db_management.sql")

(def ^:private test-db {:classname "org.postgresql.Driver"
                        :subprotocol "postgresql"
                        :subname "//localhost:5432/memoria_test"})

(defn setup-database-rollbacks
  "Creates a :each test fixture that will run each test within
  the context of a database transaction (if the strategy is :transaction)
  or will truncate all tables after the test function is executed (if the strategy
  is :truncation). This will rollback any changes applied to the database during the
  test"
  [strategy]
  (use-fixtures
    :each
    (if (= strategy :transaction)
      (fn [test-fn]
        (jdbc/with-db-transaction [tx {:connection-uri (db/database-uri :test)}]
          (jdbc/db-set-rollback-only! tx)
          (binding [db/*conn* tx]
            (test-fn))))
      (fn [test-fn]
        (binding [db/*conn* (db/test-datasource)]
          (test-fn)
          (truncate-all-tables! {} {:connection test-db}))))))
