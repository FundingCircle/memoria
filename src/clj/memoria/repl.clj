(ns memoria.repl
  (:require [memoria.db :as db]))

(defn migrate-db
  [& args]
  (db/migrate-db args))

(defn rollback-db
  [& args]
  (db/rollback-db args))

