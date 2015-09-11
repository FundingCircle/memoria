(ns memoria.app
  (:require [reagent.core :as r]
            [ajax.core :as a]
            [memoria.ajax :refer [do-get]]
            [memoria.cards-list :refer [load-latest-cards card-component cards-list-component]]
            [memoria.search-box :refer [search-box-component]]
            [memoria.add-card :refer [add-card-component]]))

(def ^:private jquery (js* "$"))

(defonce cards (r/atom []))

(defn banner-component []
  [:div {:class "banner" :key "banner"}
   [:h1 "Memoria v0.1"]])

(defn index-page-component []
  [:div
   [banner-component]
   [:div {:class "ui container main-content" :key "index-page-component"}
    [search-box-component cards]
    [cards-list-component @cards]
    [add-card-component cards]]])

(defn render-index-page []
  (r/render [index-page-component] (.getElementById js/document "memoria-container")))

(defn ^:export init []
  (render-index-page)
  (load-latest-cards cards))

