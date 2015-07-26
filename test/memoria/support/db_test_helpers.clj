(ns memoria.support.db-test-helpers
  (:require [memoria.config :as config]
            [clojure.test :refer [use-fixtures]]
            [korma.db :refer [defdb postgres transaction rollback]]))

(defn setup-db []
  (defdb db (postgres config/test-db-map)))

(defn setup-db-test []
  (setup-db)
  (use-fixtures
    :each
    (fn [f]
      (transaction
        (f)
        (rollback)))))
