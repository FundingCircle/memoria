(ns memoria.formatting
  (:require [clojure.string :as s]
            [cljs-time.format :as time-format]))

(defn truncate
  "Will reduce the contents of a card to n characters and if it gets reduced add on ..."
  [n s]
  (s/join [(if (> (count s) n) (str (subs s 0 n) "...") s)]))

(defn strip-images [contents]
   (s/replace contents #"!\[.+\]\(.+\)" ""))

(defn format-datetime-string
  "Formats a datetime string in the ISO8601 format
  (example 2015-09-19T23:58:01.846Z) to a string
  with the format dd/MM/yyyy HH:mm:ss."
  [string]
  (let [iso-formatter (time-format/formatters :date-time)
        datetime (time-format/parse iso-formatter string)]
    (time-format/unparse (time-format/formatter "dd/MM/yyyy HH:mm:ss")
                         datetime)))
