(ns memoria.state
  (:require (reagent.core :as r)))

(defonce cards-atom (r/atom []))
(defonce user-details (r/atom nil))
