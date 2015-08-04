(ns memoria.handlers.cards
  (:require [memoria.entities.cards :as cards]
            [memoria.handlers.basic :refer [not-found-response]]
            [memoria.support.debugging :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.walk :refer [keywordize-keys]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defn index [req]
  {:status 200
   :body (cards/all)
   :headers {"Content-Type" "application/json"}})

(defn show [id]
  (let [card (cards/find-by-id (Integer. id))]
    (if (some? card)
      {:status 200
       :body card}
      (not-found-response (str "Could not find a card with id " id)))))

(defn create [attrs]
  (let [attrs (keywordize-keys attrs)
        card (cards/insert attrs)]
    (if (:errors card)
      {:status 422
       :body card}
      {:status 201
       :body card})))

(defn update-card [id attrs]
  (let [id (Integer. id)
        attrs (keywordize-keys attrs)
        card (cards/update-by-id id attrs)]
    (if (:errors card)
      {:status 422
       :body card}
      {:status 200
       :body card})))

(defn delete-card [id]
  (let [id (Integer. id)]
    (cards/delete-by-id id)
    {:status 200
     :body {}}))

(defroutes cards-routes
  (GET "/cards" req (index req))
  (GET "/cards/:id" [id :as req] (show id))
  (POST "/cards" {body :body :as req} (create body))
  (PATCH "/cards/:id" {body :body {id :id} :params} (update-card id body))
  (DELETE "/cards/:id" [id :as req] (delete-card id))
  (route/not-found {:status 404 :body {}}))
