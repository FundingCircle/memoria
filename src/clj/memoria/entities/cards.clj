(ns memoria.entities.cards
  (:require [clojure.string :as s]
            [clj-time.core :as t]
            [clj-time.jdbc]
            [yesql.core :refer [defqueries]]
            [bouncer.core :as b]
            [bouncer.validators :as v]))

(defqueries "sql/cards.sql")

(defn- run-insertion [db f attrs]
  (-> (f (assoc attrs :created_at (t/now)) {:connection db})
      (dissoc :tsv)))

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
  "Finds a card with the given id. Accepts an option to force the search
  to consider soft-deleted cards."
  ([db id] (find-by-id db id false))
  ([db id search-deleted]
   (first (find-card-by-id {:id id :deleted search-deleted} {:connection db}))))

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
  [db {:keys [title contents tags]}]
  (let [attrs (validate {:title title :contents contents :tags tags})]
    (if (valid? attrs)
      (run-insertion db insert-card<! attrs)
      attrs)))

(defn update-by-id
  "Updates the card having the given id with the new attributes"
  [db id attrs]
  (let [c (find-by-id db id)
        attrs (validate (merge c attrs))]
    (if (valid? attrs)
      (run-insertion db insert-card-with-ancestor<! attrs)
       attrs)))

(defn delete-by-id
  "Deletes the card having the given id"
  [db id]
  (soft-delete-card-by-id! {:id id} {:connection db}))

