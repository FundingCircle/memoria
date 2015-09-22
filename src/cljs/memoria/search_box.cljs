(ns memoria.search-box
  (:require [reagent.core :as r]
            [memoria.cards-state :refer [cards-atom]]
            [memoria.ajax :refer [do-get]]
            [memoria.data-binding :refer [bind-input]]))

(defn search-box-component []
  (let [search-term (r/atom nil)
        on-search-submit (fn [event search-term]
                           (.preventDefault event)
                           (do-get "/search-cards"
                                        #(reset! cards-atom %1)
                                        {:q search-term}))]

    [:div {:class "ui search search-box grid" :key "search-box-component"}
     [:form {:class "column" :action "#" :on-submit #(on-search-submit %1 @search-term)}
      [:div {:class "ui input search-input"}
       [:input {:class "prompt"
                :type "text"
                :placeholder "Search for cards..."
                :on-change (bind-input search-term)}]]]]))
