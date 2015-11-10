(ns memoria.edit-card
  (:require [reagent.core :as r]
            [memoria.state :refer [cards-atom]]
            [memoria.data-binding :refer [bind-input]]
            [memoria.modal :as modal]
            [memoria.ajax :as ajax]))

(def ^:private jquery (js* "$"))

(def ^:private title-atom (r/atom nil))
(def ^:private contents-atom (r/atom nil))
(def ^:private tags-atom (r/atom nil))
(def ^:private card-atom (r/atom nil))

(defn set-state [card]
  (reset! title-atom (:title card))
  (reset! contents-atom (:contents card))
  (reset! tags-atom (:tags card)))

(defn- on-form-submit [event]
  (.preventDefault event)
  (let [params {:title @title-atom
                :contents @contents-atom
                :tags @tags-atom}]
    (ajax/do-post (str "/cards/" (:id @card-atom))
             (fn [resp]
               (ajax/load-latest-cards #(reset! cards-atom %1))
               (modal/close-edit-modal))
             params)))

(defn- cancel-edition [event]
  (.preventDefault event)
  (modal/close-edit-modal)
  (modal/open-modal))

(defn edit-card-modal-component []
  [:div {:key "add-card-modal" :class "container memoria-modal"}
   [:h2 {:class "ui aligned header"} (str "Editing card " @title-atom)]
   [:div {:class "ui divider"}]

   [:form {:class "ui small form"
           :action "#"
           :on-submit #(on-form-submit %1)}
    [:div {:class "required field"}
     [:label "Title"]
     [:input {:value @title-atom :type "text" :on-change (bind-input title-atom)}]]
    [:div {:class "required field" :required true}
     [:label "Content"]
     [:textarea {:value @contents-atom :rows "5" :on-change (bind-input contents-atom)}]]
    [:div {:class "field"}
     [:label "Tags"]
     [:input {:type "text"
              :value @tags-atom
              :on-change (bind-input tags-atom)
              :placeholder "tags are separated by spaces or commas"}]]
    [:button {:class "ui center aligned button" :on-click cancel-edition} "Cancel"]
    [:button {:class "ui center aligned button blue" :type "submit"} "Submit"]]])

(defn show-edit-modal [card]
  (reset! card-atom card)
  (set-state card)
  (modal/open-edit-modal))
