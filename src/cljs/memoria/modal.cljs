(ns memoria.modal)

(def ^:private jquery (js* "$"))
(def ^:private modal-element (jquery "#modal"))
(def ^:private edit-modal-element (jquery "#edit-modal"))

(defn open-modal []
  (-> modal-element (.modal "show")))

(defn close-modal []
  (-> modal-element (.modal "hide")))

(defn open-edit-modal []
  (-> edit-modal-element (.modal "show")))

(defn close-edit-modal []
  (-> edit-modal-element (.modal "hide")))
