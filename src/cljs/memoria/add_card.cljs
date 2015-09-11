(ns memoria.add-card
  (:require [reagent.core :as r]
            [memoria.ajax :refer [do-get do-post]]
            [memoria.modal :as modal]
            [memoria.data-binding :refer [bind-input]]
            [memoria.cards-list :refer [load-latest-cards]]))

(def ^:private jquery (js* "$"))

(def ^:private title-atom (r/atom nil))
(def ^:private contents-atom (r/atom nil))
(def ^:private tags-atom (r/atom nil))

(defn- reset-inputs []
  (reset! title-atom nil)
  (reset! contents-atom nil)
  (reset! tags-atom nil))

(defn- on-form-submit [event cards-atom]
  (.preventDefault event)
  (let [params {:title @title-atom
                :contents @contents-atom
                :tags @tags-atom}]
    (do-post "/cards"
             (fn [resp]
               (load-latest-cards cards-atom)
               (reset-inputs)
               (modal/close-modal))
             params)))

(defn add-card-modal [cards-atom]
  [:div {:key "add-card-modal" :class "container memoria-modal"}
   [:h2 {:class "ui aligned header"} "Add new card"]
   [:div {:class "ui divider"}]

   [:form {:class "ui small form"
           :action "#"
           :on-submit #(on-form-submit %1 cards-atom)}
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
    [:button {:class "ui center aligned button blue" :type "submit"} "Submit"]]])

(defn add-card-button [cards-atom]
  (let [on-click (fn []
                   (r/render [add-card-modal cards-atom] (.getElementById js/document "modal"))
                   (modal/open-modal))]
    [:button {:class "circular ui icon button massive" :key "add-card-button" :on-click on-click}
     [:i {:class "icon plus purple"}]]))

(defn add-card-component [cards-atom]
  [:div {:class "add-card" :key "add-card-component"}
   [add-card-button cards-atom]])

