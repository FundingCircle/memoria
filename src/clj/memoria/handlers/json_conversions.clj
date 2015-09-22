(ns memoria.handlers.json-conversions
  (:require [cheshire.generate :refer [JSONable to-json]]))

(extend-protocol cheshire.generate/JSONable
  org.joda.time.DateTime
  (to-json [t jg]
    (cheshire.generate/write-string jg (str t))))

