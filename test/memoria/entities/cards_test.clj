(ns memoria.entities.cards-test
  (:require [memoria.entities.cards :as cards]
            [memoria.support.db-test-helpers :as db-helpers]
            [clojure.test :refer :all]))

(db-helpers/setup-db-test :transaction)

(def card-attributes {:title "A Card" :contents "These are the card's contents"})

(deftest validations
  (let [card (cards/validate {})]
    (testing "Validates title presence"
      (is (= (get-in card [:errors :title])
             '("title must be present"))))

    (testing "Validates contents presence"
      (is (= (get-in card [:errors :contents])
             '("contents must be present"))))))

(deftest finding-cards-by-id
  (let [card (cards/insert card-attributes)]
    (testing "When there is a card with the given id"
      (is (some? (cards/find-by-id (:id card)))))

    (testing "When there is no card with the given id"
      (is (nil? (cards/find-by-id (+ 1 (:id card))))))))

(deftest listing-all-cards
  (dotimes [n 3] (cards/insert card-attributes))
  (is (= (map #(:title %1) (cards/all))
         ["A Card" "A Card" "A Card"])))

(deftest cards-insertion
  (testing "Inserting a new card increases the total cards count"
    (let [initial-count (cards/cnt)]
      (cards/insert card-attributes)
      (is (= (cards/cnt)
             (+ 1 initial-count)))))

  (testing "The inserted card has the correct title"
    (let [card (cards/insert card-attributes)]
      (is (= (:title card) "A Card"))))

  (testing "The inserted card has the correct contents"
    (let [card (cards/insert card-attributes)]
      (is (= (:contents card)
             "These are the card's contents"))))

  (testing "Fails if the attributes are invalid"
    (let [card (cards/insert {})]
      (is (= (:id card) nil))
      (is (some? (:errors card))))))

(deftest update-cards
  (testing "Updates the database record for the card"
    (let [card (cards/insert card-attributes)
          updated-card (cards/update-by-id (:id card) {:title "New title" :contents "New contents"})
          reloaded-card (cards/find-by-id (:id card))]
      (is (= (:title reloaded-card) "New title"))
      (is (= (:contents reloaded-card) "New contents"))))

  (testing "Returns the updated card"
    (let [card (cards/insert card-attributes)
          updated-card (cards/update-by-id (:id card) {:title "New title" :contents "New contents"})]
      (is (= (:title updated-card) "New title"))

      (is (= (:contents updated-card) "New contents"))))

  (testing "Fails if the attributes are invalid"
    (let [card (cards/insert card-attributes)
          updated-card (cards/update-by-id (:id card) {:title nil})]
      (is (some? (:errors updated-card))))))

(deftest delete-cards
  (let [card (cards/insert card-attributes)]
    (testing "Deletes the card having the given id"
    (cards/delete-by-id (:id card))
    (is (nil? (cards/find-by-id (:id card)))))))
