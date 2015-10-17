(ns memoria.cards-list
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [reagent.core :as r]
            [memoria.formatting :as formatting]
            [memoria.delete-card :as delete-card]
            [memoria.cards-state :refer [cards-atom]]
            [memoria.modal :as modal]
            [memoria.edit-card :as edit-card]
            [memoria.ajax :as ajax]))

(def ^:private jquery (js* "$"))

(defn open-edit-modal [card]
  (edit-card/show-edit-modal card))

(defn created-at-component [card]
  (when (some? (:created_at card))
    [:span {:class "created-at"} (-> card
                                     :created_at
                                     formatting/format-datetime-string)]))

(defn card-modal-component [card]
  (delete-card/reset-state)
  [:div {:key "show-card-modal" :class "container memoria-modal memoria-card"}
   [:div {:class "ui header"}
    [:h1 {:class "title"} (:title card)]
    [:span {:class "tags"} (:tags card)]]
   [created-at-component card]
   [:div {:class "ui divider"}]
   [:div#markdown-content {:class "card-contents"}]
   [delete-card/delete-button-component card]
   [:button {:class "circular ui icon button edit-card" :title "Edit card" :on-click #(open-edit-modal card)}
     [:i {:class "icon write"}]]])

(defn markdown-component [contents]
  (fn [contents]
    [:div {:dangerouslySetInnerHTML
           {:__html (-> contents str js/marked)}}]))

(defn card-component [card]
  (let [stripped-contents (formatting/strip-images (:contents card))
        on-title-clicked (fn [event]
                           (let [card-id (-> event .-target jquery (.data "id"))
                                 url (str "/cards/" card-id)]
                             (ajax/do-get url (fn [response]
                                           (r/render [card-modal-component response] (.getElementById js/document "modal"))
                                           (r/render [markdown-component (:contents card)] (.getElementById js/document "markdown-content"))
                                           (-> (jquery "#modal")
                                               (.modal "show"))))))]

    [:div {:class "eight wide column memoria-cards" :key (:id card)}
     [:div {:class "memoria-card ui container raised padded segment purple" :id (str "card-thumbnail-" (:id card))}
      [:div {:class "ui header"}
       [:h2 {:class "title"}
        [:a {:href "#"
             :data-id (:id card)
             :on-click on-title-clicked} (:title card)]]
       [:span {:class "tags"} (:tags card)]
       [created-at-component card]]
      [:div {:class "ui divider"}]
      [:div {:class "card-contents"} [markdown-component (formatting/truncate 400 stripped-contents)]]]]))

(defn cards-list-component []
  [:div#cards-container {:class "ui stackable sixteen grid container" :key "cards-list-container"}
   (for [card @cards-atom] [card-component card])])

