(ns memoria.app
  (:require [reagent.core :as r]
            [ajax.core :as a]
            [memoria.state :refer [cards-atom user-details]]
            [memoria.ajax :as ajax]
            [memoria.cards-list :refer [card-component cards-list-component]]
            [memoria.search-box :refer [search-box-component]]
            [memoria.add-card :refer [add-card-component]]
            [memoria.edit-card :refer [edit-card-modal-component]]))

(def ^:private jquery (js* "$"))

(defn auth-button-component
  "When this button is clicked, the Google+ authentication is initialised, causing
  the Google popup to be displayed."
  []
   [:button {:id "auth-button" :class "ui button ":on-click js/handleAuthClick}
    [:span.icon]
    [:span.button-text "Sign in with Google"]])

(defn user-links-component
  []
  [:div.user-links
   [:span.user-name (:display_name @user-details)]
   [:span.user-photo [:img {:src (get @user-details :photo_url)}]]])

(defn banner-component []
  [:div {:class "banner" :key "banner"}
   [:div.banner-title
    [:h1 "Memoria v0.1"]]
   (if @user-details
     [user-links-component]
     [auth-button-component])])

(defn index-page-component
  "If the user is authenticated the list of the latest cards is displayed.
  Otherwise, the auth button is displayed."
  []
  [:div
   [banner-component]
   (when @user-details
     [:div {:class "ui container main-content" :key "index-page-component"}
      [search-box-component]
      [cards-list-component]
      [add-card-component]])])

(defn render-index-page []
  (r/render [index-page-component] (.getElementById js/document "memoria-container")))

(defn render-edit-modal []
  (r/render [edit-card-modal-component] (.getElementById js/document "edit-modal")))

(defn render-auth-button []
  (r/render [auth-button-component] (.getElementById js/document "memoria-container")))

(defn load-page []
  (render-index-page)
  (render-edit-modal))

(defn ^:export auth
  "Receives the response from calling the Google+ API with the user's details.

  Having the user's details, the following actions are performed:

  - Resets the user-details atom, which causes the latest cards to be displayed.
  - Does an Ajax POST request to register the user.
  - Loads the latest cards and resets the cards atom."
  [user-details-response]
  (let [details (js->clj user-details-response :keywordize-keys true)
        registered-user (ajax/do-post "/auth" (fn [response]
                                                (.log js/console response)
                                                (reset! user-details response)) details)]
    ;; (reset! user-details (assoc details :id (:id registered-user)))
    (ajax/load-latest-cards #(reset! cards-atom %1))))

(defn ^:export init
  []
  (load-page))
