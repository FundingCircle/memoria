(ns memoria.entities.cards
  (:require [korma.core :as k]
            [bouncer.core :as b]
            [bouncer.validators :as v]))

(k/defentity cards)

(defn validate [card]
  (let [errors (first (b/validate card
                                :title v/required
                                :contents v/required))]
    (if (some? errors)
      (assoc card :errors errors)
      card)))

(defn valid? [card]
  (nil? (:errors card)))

(defn cnt
  "Returns the total number of card records in
  the database"
  []
  (:cnt (first (k/select cards (k/aggregate (count :*) :cnt)))))

(defn find-by-id
  "Finds a card with the given id"
  [id]
  (first (k/select cards (k/where {:id id}))))

(defn all
  "Returns all existent cards"
  []
  (k/select cards))

(defn insert
  "Inserts a new card record in the database"
  [{:keys [title contents]}]
  (let [attrs (validate {:title title :contents contents})]
    (if (valid? attrs)
      (k/insert cards (k/values attrs))
      attrs)))

(defn update-by-id
  "Updates the card having the given id with the new attributes"
  [id attrs]
  (let [c (find-by-id id)
         attrs (validate (merge c attrs))]
    (when (valid? attrs)
      (k/update cards
                (k/set-fields attrs)
                (k/where {:id id})))
    attrs))

(defn delete-by-id
  "Deletes the card having the given id"
  [id]
  (k/delete cards (k/where {:id id})))

