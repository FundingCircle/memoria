(ns memoria.entities.validations
  (:require [bouncer.core :as b]))

(defn validate-entity
  "Applies all validations to the entity map, adding a new :errors keyword/value
  to it if there one or more of the validations fail.
  Returns the resulting entity."
  [entity validations]
  (let [validation-function (partial b/validate entity)
        errors (first (apply validation-function validations))]
    (if (some? errors)
      (assoc entity :errors errors)
      entity)))

(defn valid? [card]
  (nil? (:errors card)))
