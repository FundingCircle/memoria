(ns memoria.entities.cards
  (:require [clojure.string :as s]
            [yesql.core :refer [defqueries]]
            [bouncer.core :as b]
            [bouncer.validators :as v]))

(defqueries "sql/cards.sql")

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
  [db]
  (:count (first (count-cards {} {:connection db}))))

(defn find-by-id
  "Finds a card with the given id"
  [db id]
  (first (find-card-by-id {:id id} {:connection db})))

(defn latest
  "Returns latest cards"
  ([db] (latest db 1 10))
  ([db page limit] (select-latest-cards {:offset (* (dec page) limit) :limit limit} {:connection db})))

(defn search
  "Searches for cards"
  ([db q] (search db q 1 10))
  ([db q page limit]
    (let [search-term (s/join " & " (s/split q #" "))]
      (search-cards {:query search-term :offset (* (dec page) limit) :limit limit} {:connection db}))))

(defn insert
  "Inserts a new card record in the database"
  [db {:keys [title contents]}]
  (let [attrs (validate {:title title :contents contents})]
    (if (valid? attrs)
      (dissoc (insert-card<! attrs {:connection db}) :tsv)
      attrs)))

(defn update-by-id
  "Updates the card having the given id with the new attributes"
  [db id attrs]
  (let [c (find-by-id db id)
        attrs (validate (merge c attrs))]
    (if (valid? attrs)
       (dissoc (insert-card-with-ancestor<! attrs {:connection db}) :tsv)
       attrs)))

(defn delete-by-id
  "Deletes the card having the given id"
  [db id]
  (delete-card-by-id! {:id id} {:connection db}))
