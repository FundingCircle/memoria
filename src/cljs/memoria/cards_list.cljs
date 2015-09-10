(ns memoria.cards-list
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [memoria.ajax :refer [do-get]]))

(def ^:private jquery (js* "$"))

(defn- max-length
  "Will reduce the contents of a card to n characters and if it gets reduced add on ..."
  [n s]
  (s/join [(if (> (count s) n) (str (subs s 0 n) "...") s)]))

(defn card-modal-component [card]
  [:div {:class "ui container"} (:contents card)])

(defn card-component [card]
  (let [on-title-clicked (fn [event]
                           (let [card-id (-> event .-target jquery (.data "id"))
                                 url (str "/cards/" card-id)
                                 card (do-get url #(r/render [card-modal-component %1] (.getElementById js/document "modal")))]
                             (-> (jquery "#modal")
                                 (.modal "show"))))]

    [:div {:class "eight wide column" :key (:id card)}
     [:div {:class "memoria-card ui container raised padded segment purple"}
      [:div {:class "ui header"}
       [:h2 [:a {:href "#"
                 :data-id (:id card)
                 :on-click on-title-clicked} (:title card)]]
       [:span {:class "tags"} (:tags card)]]
      [:div {:class "ui divider"}]
      [:div {:class "card-contents"} (max-length 400 (:contents card))]]]))

(defn cards-list-component [cards]
  [:div#cards-container {:class "ui grid sixteen container" :key "cards-list-container"}
   (for [card cards] [card-component card])])

