(ns memoria.add-card
  (:require [reagent.core :as r]
            [memoria.ajax :refer [do-get do-post]]
            [memoria.modal :as modal]
            [memoria.data-binding :refer [bind-input]]
            [memoria.app :as app]))

(defn- on-form-submit [event params]
  (.preventDefault event)
  (do-post "/cards"
           (fn [resp]
             (app/load-latest-cards)
             (modal/close-modal))
           params))

(defn add-card-modal []
  (let [title (r/atom nil)
        contents (r/atom nil)
        tags (r/atom nil)]

    [:div {:key "add-card-modal" :class "container memoria-modal"}
     [:h2 {:class "ui aligned header"} "Add new card"]
     [:div {:class "ui divider"}]

     [:form {:class "ui small form"
             :action "#"
             :on-submit #(on-form-submit %1 {:title @title
                                             :contents @contents
                                             :tags @tags})}
      [:div {:class "required field"}
       [:label "Title"]
       [:input {:type "text" :on-change (bind-input title)}]]
      [:div {:class "required field" :required true}
       [:label "Content"]
       [:textarea {:rows "5" :on-change (bind-input contents)}]]
      [:div {:class "field"}
       [:label "Tags"]
       [:input {:type "text" :on-change (bind-input tags) :placeholder "tags are separated by spaces or commas"}]]
      [:button {:class "ui center aligned button blue" :type "submit"} "Submit"]]]))

(defn add-card-button []
  (let [on-click (fn []
                   (r/render [add-card-modal] (.getElementById js/document "modal"))
                   (modal/open-modal))]
    [:button {:class "circular ui icon button massive" :key "add-card-button" :on-click on-click}
     [:i {:class "icon plus purple"}]]))

(defn add-card-component []
  [:div {:class "add-card" :key "add-card-component"}
   [add-card-button]])

