(ns memoria.handlers.cards
  (:require [memoria.entities.cards :as cards]
            [memoria.handlers.basic :refer [not-found-response]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defn index [req]
  {:status 200
   :body (cards/all)
   :headers {"Content-Type" "application/json"}})

(defn show [id]
  (let [card (cards/find-by-id (Integer. id))]
    (if (some? card)
      {:status 200
       :body card
       :headers {"Content-Type" "application/json"}}
      (not-found-response (str "Could not find a card with id " id)))))

(defn insert-card [req]
  )

(defroutes cards-routes
  (GET "/cards" req (index req))
  (GET "/cards/:id" [id :as req] (show id))
  (POST "/cards" {params :params} (insert-card params)))
