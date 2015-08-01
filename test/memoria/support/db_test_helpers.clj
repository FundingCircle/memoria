(ns memoria.support.db-test-helpers
  (:require [memoria.config :as config]
            [clojure.test :refer [use-fixtures]]
            [korma.db :refer [defdb postgres transaction rollback]]
            [korma.core :as k]))

(defn setup-db []
  (defdb db (postgres config/test-db-map)))

(defn- truncate-tables []
  (let [tables ["cards"]
        sql (str "TRUNCATE " (clojure.string/join "," ["cards"]) " CASCADE")]
    (k/exec-raw sql)))

(defn setup-db-test [rollback-strategy]
  (let [rolback-strategy (or rollback-strategy :transaction)]
    (setup-db)
    (if (= rollback-strategy :transaction)
      (use-fixtures
        :each
        (fn [f]
          (transaction
            (f)
            (rollback))))
      (use-fixtures
        :each
        (fn [f]
          (f)
          (truncate-tables))))))
