(ns memoria.app
  (:require [reagent.core :as r]
            [ajax.core :as a]
            [clojure.string :as s]
            [memoria.ajax :refer [do-get]]
            [memoria.cards-list :refer [load-latest-cards card-component cards-list-component]]
            [memoria.search-box :refer [search-box-component]]
            [memoria.add-card :refer [add-card-component]]))

(def ^:private jquery (js* "$"))

(defn- max-length
  "Will reduce the contents of a card to n characters and if it gets reduced add on ..."
  [n s]
  (s/join [(if (> (count s) n) (str (subs s 0 n) "...") s)]))

(defonce cards (r/atom []))

(defn banner-component []
  [:div {:class "banner" :key "banner"}
   [:h1 "Memoria v0.1"]])

(defn index-page-component []
  [:div
   [add-card-component cards]
   [banner-component]
   [:div {:class "ui container main-content" :key "index-page-component"}
    [search-box-component cards]
    [cards-list-component @cards]]])

(defn render-index-page []
  (r/render [index-page-component] (.getElementById js/document "memoria-container")))

(defn ^:export init []
  (render-index-page)
  (load-latest-cards cards))

