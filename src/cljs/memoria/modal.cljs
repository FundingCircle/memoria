(ns memoria.modal)

(def ^:private jquery (js* "$"))
(def ^:private modal-element (jquery "#modal"))

(defn open-modal []
  (-> modal-element (.modal "show")))

(defn close-modal []
  (-> modal-element (.modal "hide")))
