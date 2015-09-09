(ns memoria.tags
  (:require [selmer.filters :as filters]
            [selmer.parser :as parser]
            [clojure.string :as s]))

(defn- max-length [n s] (s/join [(apply str (take n s))  "..."]))

(filters/add-filter! :truncate #(max-length 400 %1))

