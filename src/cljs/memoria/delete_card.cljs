(ns memoria.delete-card
  (:require [reagent.core :as r]
            [memoria.ajax :as ajax]
            [memoria.modal :as modal]
            [memoria.cards-state :refer [cards-atom]]))

(def ^:private delete-state (r/atom :initial))

(defn reset-state
  "Resets the delete card component to its initial state"
  []
  (reset! delete-state :initial))

(defn- on-confirm-clicked [card]
  (let [url (str "/cards/" (:id card) "/delete")]
    (ajax/do-post url (fn [response]
                        (reset-state)
                        (ajax/load-latest-cards #(reset! cards-atom %1))
                        (modal/close-modal)))))

(defn delete-button-component [card]
  (let [on-delete-clicked (fn [] (reset! delete-state :confirming))
        on-cancel-clicked (fn [] (reset-state))]

    (fn [card]
      (let [delete-button [:button {:class "circular ui icon button delete-card" :title "Delete card" :on-click on-delete-clicked}
                           [:i {:class "icon erase"}]]

            confirmation-buttons [:span
                                  [:button {:class "ui button basic red cancel" :on-click on-cancel-clicked} "Cancel"]
                                  [:button {:class "ui button red confirm" :on-click #(on-confirm-clicked card)} "I'm sure!"]]]

        [:span.delete-button-component
         (condp = @delete-state
           :initial delete-button
           :confirming confirmation-buttons)]))))
