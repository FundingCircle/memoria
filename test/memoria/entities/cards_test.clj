(ns memoria.entities.cards-test
  (:require [memoria.entities.cards :as cards]
            [memoria.support.db-test-helpers :as db-helpers]
            [clojure.test :refer :all]))

(db-helpers/setup-db-test)

(def card-attributes {:title "A Card" :contents "These are the card's contents"})

(deftest finding-cards-by-id
  (let [card (cards/insert card-attributes)]
    (testing "When there is a card with the given id"
      (is (some? (cards/find-by-id (:id card)))))

    (testing "When there is no card with the given id"
      (is (nil? (cards/find-by-id (+ 1 (:id card))))))))

(deftest listing-all-cards
  (dotimes [n 3] (cards/insert card-attributes))
  (is (= (map #(:title %1) (cards/all)) ["A Card" "A Card" "A Card"])))

(deftest cards-insertion
  (testing "Inserting a new card increases the total cards count"
    (let [initial-count (cards/cnt)]
      (cards/insert card-attributes)
      (is (= (cards/cnt) (+ 1 initial-count)))))

  (testing "The inserted card has the correct title"
    (let [card (cards/insert card-attributes)]
      (is (= (:title card) "A Card"))))

  (testing "The inserted card has the correct contents"
    (let [card (cards/insert card-attributes)]
      (is (= (:contents card) "These are the card's contents")))))

(deftest update-cards
  (let [card (cards/insert card-attributes)]
    (testing "Updates the cards attributes"
      (cards/update-by-id (:id card) {:title "New title" :contents "New contents"})
      (let [updated-card (cards/find-by-id (:id card))]
        (is (= (:title updated-card) "New title"))
        (is (= (:contents updated-card) "New contents"))))))

(deftest delete-cards
  (let [card (cards/insert card-attributes)]
    (testing "Deletes the card having the given id"
    (cards/delete-by-id (:id card))
    (is (nil? (cards/find-by-id (:id card)))))))
