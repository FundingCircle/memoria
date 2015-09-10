(ns memoria.app
  (:require [reagent.core :as r]
            [ajax.core :as a]))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console
        (str "Something bad happened: " status " " status-text)))

(defonce cards (r/atom []))

(defn fetch-cards
  ([] (fetch-cards "/cards"))
  ([path] (fetch-cards path {}))
  ([path params] (a/GET path {:headers {"Content-Type" "application/json"}
                              :params params
                              :format :json
                              :response-format :json
                              :keywords? true
                              :prefix "while(1);"
                              :handler (fn [response]
                                         (reset! cards response))
                              :error-handler error-handler})))

(defn card-component [card]
  [:div {:class "eight wide column" :key (:id card)}
   [:div {:class "memoria-card ui container raised padded segment purple"}
    [:div {:class "ui header"}
     [:h2 [:a {:href "#"} (:title card)]]]
    [:div {:class "ui divider"}]
    [:div {:class "card-contents"} (:contents card)]]])

(defn cards-list-component [cards]
  [:div#cards-container {:class "ui grid sixteen container" :key "cards-list-container"}
   (for [card cards] [card-component card])])

(defn search-box-component []
  (let [search-term (r/atom nil)
        on-search-submit (fn [event search-term]
                           (.preventDefault event)
                           (fetch-cards "/search-cards" {:q search-term}))]

    [:div {:class "ui search search-box grid" :key "search-box-component"}
     [:form {:action "#" :on-submit #(on-search-submit %1 @search-term)}
      [:div {:class "ui input search-input"}
       [:input {:class "prompt"
                :type "text"
                :placeholder "Search for cards..."
                :on-change #(reset! search-term (-> %1 .-target .-value))}]]]]))

(defn index-page-component []
  [:div {:class "banner"}
   [:div {:class "ui container main-content" :key "index-page-component"}
    [search-box-component]
    [cards-list-component @cards]]])

(defn render-index-page []
  (r/render [index-page-component] (.-body js/document)))

(defn load-latest-cards []
  (fetch-cards "/cards")
  (render-index-page))

(defn init []
  (render-index-page)
  (load-latest-cards))

(init)
