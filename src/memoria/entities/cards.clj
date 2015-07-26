(ns memoria.entities.cards
  (:require [korma.core :as k]))

(k/defentity cards)

(defn cnt
  "Returns the total number of card records in
  the database"
  []
  (:cnt (first (k/select cards (k/aggregate (count :*) :cnt)))))

(defn find-by-id
  "Finds a card with the given id"
  [id]
  (first (k/select cards
                   (k/where {:id id}))))

(defn insert
  "Inserts a new card record in the database"
  [{:keys [title contents]}]
  (k/insert cards (k/values {:title title :contents contents})))

(defn update-by-id
  "Updates the card having the given id with the new attributes"
  [id {:keys [title contents]}]
  (k/update cards
            (k/set-fields {:title title :contents contents})
            (k/where {:id id})))

(defn delete-by-id
  "Deletes the card having the given id"
  [id]
  (k/delete cards (k/where {:id id})))
