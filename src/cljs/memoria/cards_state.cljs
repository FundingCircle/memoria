(ns memoria.cards-state
  (:require (reagent.core :as r)))

(defonce cards-atom (r/atom []))
