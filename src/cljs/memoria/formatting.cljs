(ns memoria.formatting
  (:require [clojure.string :as s]))

(defn truncate
  "Will reduce the contents of a card to n characters and if it gets reduced add on ..."
  [n s]
  (s/join [(if (> (count s) n) (str (subs s 0 n) "...") s)]))

(defn strip-images [contents]
   (s/replace contents #"!\[.+\]\(.+\)" ""))
