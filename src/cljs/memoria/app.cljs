(ns memoria.app
  (:require [reagent.core :as r]
            [ajax.core :as a]))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console
        (str "Something bad happened: " status " " status-text)))

(defonce cards (r/atom []))

(defn search-box-component []
  [:div {:class "ui search search-box grid" :key "search-box-component"}
   [:div {:class "ui input search-input"}
    [:input {:class "prompt" :type "text" :placeholder "Search for cards..."}]]])

(defn card-component [card]
  [:div {:class "eight wide column" :key (:id card)}
   [:div {:class "memoria-card ui container raised padded segment blue"}
    [:div {:class "ui header"}
     [:h2 [:a {:href "#"} (:title card)]]]
    [:div {:class "ui divider"}]
    [:div {:class "card-contents"} (:contents card)]]])

(defn cards-list-component [cards]
  [:div#cards-container {:class "ui grid sixteen container" :key "cards-list-container"}
   (for [card cards] [card-component card])])

(defn index-page-component [cards]
  [:div {:class "ui container main-content" :key "index-page-component"}
   [search-box-component]
   [cards-list-component cards]])

(defn render-index-page []
  (r/render [index-page-component @cards] (.-body js/document)))

(defn load-latest-cards []
  (a/GET "/cards" {:headers {"Content-Type" "application/json"}
                   :format :json
                   :response-format :json
                   :keywords? true
                   :prefix "while(1);"
                   :handler (fn [response] (.log js/console (str response))
                              (reset! cards response)
                              (render-index-page))
                   :error-handler error-handler}))

(defn update-cards [cards])

(defn init []
  (render-index-page)
  (load-latest-cards))

(init)
