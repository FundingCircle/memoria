(ns memoria.entities.cards-test
  (:require [memoria.entities.cards :as cards]
            [memoria.db :refer [*conn*]]
            [memoria.support.db-test-helpers :refer [setup-database-rollbacks]]
            [clojure.test :refer :all]))

(setup-database-rollbacks :transaction)

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
  (let [card (cards/insert *conn* card-attributes)]
    (testing "When there is a card with the given id"
      (is (some? (cards/find-by-id *conn* (:id card)))))

    (testing "When there is no card with the given id"
      (is (nil? (cards/find-by-id *conn* (+ 1 (:id card))))))))

(deftest listing-latest-cards
  (testing "Returns specified number of cards"
    (dotimes [n 3] (cards/insert *conn* card-attributes))
    (is (= (map #(:title %1) (cards/latest *conn* 2))
           ["A Card" "A Card"])))

  (testing "Only returns latest version of a card"
    (let [old-card (cards/insert *conn* {:title "Old title" :contents "old contents"})
          card (cards/insert *conn* card-attributes)]
      (cards/update-by-id *conn* (:id card) {:title "New title"})
      (cards/insert *conn* {:title "Another title" :contents "More contents"})
      (is (= (map #(:title %1) (cards/latest *conn* 3))
             ["Another title" "New title" "Old title"])))))

(deftest cards-insertion
  (testing "Inserting a new card increases the total cards count"
    (let [initial-count (cards/cnt *conn*)]
      (cards/insert *conn* card-attributes)
      (is (= (cards/cnt *conn*)
             (+ 1 initial-count)))))

  (testing "The inserted card has the correct title"
    (let [card (cards/insert *conn* card-attributes)]
      (is (= (:title card) "A Card"))))

  (testing "The inserted card has the correct contents"
    (let [card (cards/insert *conn* card-attributes)]
      (is (= (:contents card) "These are the card's contents"))))

  (testing "Fails if the attributes are invalid"
    (let [card (cards/insert *conn* {})]
      (is (= (:id card) nil))
      (is (some? (:errors card))))))

(deftest update-cards
  (testing "It does not update the database record for the card"
    (let [card (cards/insert *conn* card-attributes)
          updated-card (cards/update-by-id *conn* (:id card) {:title "New title" :contents "New contents"})
          reloaded-card (cards/find-by-id *conn* (:id card))]
      (is (= (:title reloaded-card) "A Card"))
      (is (= (:contents reloaded-card) "These are the card's contents"))))

  (testing "Creates a new record from the updated attributes"
    (let [card (cards/insert *conn* card-attributes)
          updated-card (cards/update-by-id *conn* (:id card) {:title "New title" :contents "New contents"})]
      (is (not= (:id updated-card) (:id card)))
      (is (= (:ancestor_id updated-card) (:id card)))
      (is (= (:title updated-card) "New title"))
      (is (= (:contents updated-card) "New contents"))))

  (testing "It keeps contents if unchanged"
    (let [card (cards/insert *conn* card-attributes)
          updated-card (cards/update-by-id *conn* (:id card) {:title "New title"})]
      (is (= (:title updated-card) "New title"))
      (is (= (:contents updated-card) "These are the card's contents"))))

  (testing "It keeps title if unchanged"
    (let [card (cards/insert *conn* card-attributes)
          updated-card (cards/update-by-id *conn* (:id card) {:contents "New contents"})]
      (is (= (:title updated-card) "A Card"))))

  (testing "Fails if the attributes are invalid"
    (let [card (cards/insert *conn* card-attributes)
          updated-card (cards/update-by-id *conn* (:id card) {:title nil})]
      (is (some? (:errors updated-card))))))

(deftest delete-cards
  (let [card (cards/insert *conn* card-attributes)]
    (testing "Deletes the card having the given id"
    (cards/delete-by-id *conn* (:id card))
    (is (nil? (cards/find-by-id *conn* (:id card)))))))
