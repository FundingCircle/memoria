(ns memoria.app
  (:require [reagent.core :as r]
            [ajax.core :as a]
            [clojure.string :as s]))

(def jquery (js* "$"))

(defn- max-length
  "Will reduce the contents of a card to n characters and if it gets reduced add on ..."
  [n s]
  (s/join [(if (> (count s) n) (str (subs s 0 n) "...") s)]))

(def jquery (js* "$"))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console
        (str "Something bad happened: " status " " status-text)))

(defonce cards (r/atom []))

(defn do-get
  ([path handler] (do-get path handler {}))
  ([path handler params] (a/GET path {:headers {"Content-Type" "application/json"}
                              :params params
                              :format :json
                              :response-format :json
                              :keywords? true
                              :prefix "while(1);"
                              :handler handler
                              :error-handler error-handler})))

(defn card-modal-component [card]
  (.log js/console card)
  [:div {:class "ui container"} (:contents card)])

(defn create-card [params]
  (a/POST "/cards" {:headers {"Content-Type" "application/json"}
                    :params params
                    :format :json
                    :response-format :json
                    :keywords? true
                    :handler true
                    :error-handler error-handler}))

(defn card-component [card]
  (let [on-title-clicked (fn [event]
                           (let [card-id (-> event .-target jquery (.data "id"))
                                 url (str "/cards/" card-id)
                                 card (do-get url #(r/render [card-modal-component %1] (.getElementById js/document "modal")))]
                             (-> (jquery "#modal")
                                 (.modal "show"))))]

    [:div {:class "eight wide column" :key (:id card)}
     [:div {:class "memoria-card ui container raised padded segment purple"}
      [:div {:class "ui header"}
       [:h2 [:a {:href "#"
                 :data-id (:id card)
                 :on-click on-title-clicked} (:title card)]]
       [:span {:class "tags"} (:tags card)]]
      [:div {:class "ui divider"}]
      [:div {:class "card-contents"} (max-length 400 (:contents card))]]]))

(defn add-card-modal []
  (let [title (r/atom nil)
        content (r/atom nil)
        tags (r/atom nil)
        on-add-card-submit (fn [event params]
                           (.preventDefault event)
                           (create-card {:title title
                                         :content content
                                         :tags tags}))]

    [:div {:class "ui segment modal" :key "add-card-modal"}
     [:h2 {:class "ui center aligned icon header"}
      [:i {:class "circular plus purple icon"}]
      [:content {:class "content"} "Add card"]]

    [:form {:class "ui small form"}
     [:div {:class "required field"}
      [:label "Title"]
      [:input {:type "text"}]]
     [:div {:class "required field"}
      [:label "Content"]
      [:textarea {:rows "5"}]]
     [:div {:class "field"}
      [:label "Tags"]
      [:input {:type "text"}]]
     [:button {:class "ui center aligned button" :type "submit"} "Submit"]]]))


(defn add-card-button [show-modal]
  [:button {:class "circular ui icon button massive" :key "add-card-button" :on-click show-modal}
   [:i {:class "icon plus purple"}]])

(defn add-card-component []
  (let [on-click (fn [] (-> (jquery ".ui.modal")
                            (.modal "show")))]

  [:div {:class "add-card" :key "add-card-component"}
   [add-card-button on-click]
   [add-card-modal]]))


(defn cards-list-component [cards]
  [:div#cards-container {:class "ui grid sixteen container" :key "cards-list-container"}
   (for [card cards] [card-component card])])

(defn search-box-component []
  (let [search-term (r/atom nil)
        on-search-submit (fn [event search-term]
                           (.preventDefault event)
                           (do-get "/search-cards"
                                        #(reset! cards %1)
                                        {:q search-term}))]

    [:div {:class "ui search search-box grid" :key "search-box-component"}
     [:form {:action "#" :on-submit #(on-search-submit %1 @search-term)}
      [:div {:class "ui input search-input"}
       [:input {:class "prompt"
                :type "text"
                :placeholder "Search for cards..."
                :on-change #(reset! search-term (-> %1 .-target .-value))}]]]]))

(defn index-page-component []
  [:div
   [add-card-component]
   [:div {:class "banner" :key "banner"}
    [:h1 "Memoria v0.1"]]
   [:div {:class "ui container main-content" :key "index-page-component"}
    [search-box-component]
    [cards-list-component @cards]]])

(defn render-index-page []
  (r/render [index-page-component] (.getElementById js/document "memoria-container")))

(defn load-latest-cards []
  (do-get "/cards" #(reset! cards %1))
  (render-index-page))

(defn ^:export init []
  (render-index-page)
  (load-latest-cards))

