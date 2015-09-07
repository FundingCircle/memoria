(ns memoria.repl
  (:require [memoria.db :as db]
            [clojure.test :as t]))

(defn migrate-db
  [& args]
  (db/migrate-db args))

(defn rollback-db
  [& args]
  (db/rollback-db args))

(defn run-tests
  [_n]
  (t/run-tests _n))

