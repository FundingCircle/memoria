(ns memoria.cards-list
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [memoria.formatting :as formatting]
            [memoria.ajax :refer [do-get]]))

(def ^:private jquery (js* "$"))

(defn load-latest-cards [cards-atom]
  (do-get "/cards" #(reset! cards-atom %1)))

(defn card-modal-component [card]
  [:div {:key "show-card-modal" :class "container memoria-modal memoria-card"}
   [:div {:class "ui header"}
    [:h1 {:class "title"} (:title card)]
    [:span {:class "tags"} (:tags card)]]
   [:div {:class "ui divider"}]
   [:div#markdown-content {:class "card-contents"}]])

(defn markdown-component [contents]
  (fn [contents]
    [:div {:dangerouslySetInnerHTML
           {:__html (-> contents str js/marked)}}]))

(defn card-component [card]
  (let [on-title-clicked (fn [event]
                           (let [card-id (-> event .-target jquery (.data "id"))
                                 url (str "/cards/" card-id)
                                 card (do-get url (fn [response]
                                                    (r/render [card-modal-component response] (.getElementById js/document "modal"))
                                                    (r/render [markdown-component (:contents card)] (.getElementById js/document "markdown-content"))))]

                             (-> (jquery "#modal")
                                 (.modal "show"))))]

    [:div {:class "eight wide column memoria-cards" :key (:id card)}
     [:div {:class "memoria-card ui container raised padded segment purple"}
      [:div {:class "ui header"}
       [:h2 {:class "title"}
        [:a {:href "#"
             :data-id (:id card)
             :on-click on-title-clicked} (:title card)]]
       [:span {:class "tags"} (:tags card)]]
      [:div {:class "ui divider"}]
      [:div {:class "card-contents"} [markdown-component (formatting/truncate 400 (:contents card))]]]]))

(defn cards-list-component [cards]
  [:div#cards-container {:class "ui grid sixteen container" :key "cards-list-container"}
   (for [card cards] [card-component card])])
