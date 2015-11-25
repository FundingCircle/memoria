(ns memoria.cards-list
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [reagent.core :as r]
            [memoria.formatting :as formatting]
            [memoria.delete-card :as delete-card]
            [memoria.state :refer [cards-atom]]
            [memoria.modal :as modal]
            [memoria.edit-card :as edit-card]
            [memoria.ajax :as ajax]
            [clojure.string :as str]))

(def ^:private jquery (js* "$"))

(defn open-edit-modal [card]
  (edit-card/show-edit-modal card))

(defn- card-created-at [card]
  (-> card
      :created_at
      formatting/format-datetime-string))

(defn url-hash [card]
  (let [title (-> card
                  :title
                  str/lower-case
                  (str/replace #" " "-"))]
    (str "card-" (:id card) "-" title)))

(defn created-at-component [card]
  [:span {:class "created-at"} (card-created-at card)])

(defn author-component [card]
  [:span {:class "created-at"} (str "Last edited by "
                                    (:user_name card)
                                    " at "
                                    (card-created-at card))])

(defn card-modal-component [card]
  (delete-card/reset-state)
  [:div {:key "show-card-modal" :class "container memoria-modal memoria-card"}
   [:div {:class "ui header"}
    [:h1 {:class "title"} (:title card)]
    [:span {:class "tags"} (:tags card)]]
   [author-component card]
   [:div {:class "ui divider"}]
   [:div#markdown-content {:class "card-contents"}]
   [delete-card/delete-button-component card]
   [:button {:class "circular ui icon button edit-card" :title "Edit card" :on-click #(open-edit-modal card)}
    [:i {:class "icon write"}]]])

(defn markdown-component [contents]
  (fn [contents]
    [:div {:dangerouslySetInnerHTML
           {:__html (-> contents str js/marked)}}]))

(defn open-card-modal
  [card-id]
  (let [url (str "/cards/" card-id)]
    (ajax/do-get url (fn [response]
                       (r/render [card-modal-component response] (.getElementById js/document "modal"))
                       (r/render [markdown-component (:contents card)] (.getElementById js/document "markdown-content"))
                       (-> (jquery "#modal")
                           (.modal "show"))))))

(defn card-component [card]
  (let [stripped-contents (formatting/strip-images (:contents card))
        on-title-clicked (fn [event]
                           (.preventDefault event)
                           (set! (.-hash js/window.location) (url-hash card))
                           (open-card-modal (:id card)))]
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

