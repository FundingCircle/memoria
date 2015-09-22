(ns memoria.entities.cards-test
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [memoria.entities.cards :as cards]
            [memoria.db :refer [*conn*]]
            [memoria.support.db-test-helpers :refer [setup-database-rollbacks]]
            [clojure.test :refer :all]))

(setup-database-rollbacks :transaction)

(def card-attributes {:title "A Card" :contents "These are the card's contents" :tags "tag1 tag2 tag3"})

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
  (testing "Returns latest 10 cards when no number is specified"
    (dotimes [n 11] (cards/insert *conn* card-attributes))
    (is (= (count (cards/latest *conn*))
           10)))

  (testing "Returns specified number of cards"
    (dotimes [n 3] (cards/insert *conn* card-attributes))
    (is (= (map #(:title %1) (cards/latest *conn* 1 2))
           ["A Card" "A Card"])))

  (testing "Only returns latest version of a card"
    (let [old-card (cards/insert *conn* {:title "Old title" :contents "old contents"})
          card (cards/insert *conn* card-attributes)]
      (cards/update-by-id *conn* (:id card) {:title "New title"})
      (cards/insert *conn* {:title "Another title" :contents "More contents"})
      (is (= (map #(:title %1) (cards/latest *conn* 1 3))
             ["Another title" "New title" "Old title"])))))

(deftest cards-insertion-test
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

  (testing "The inserted card has the correct creation time"
    (let [now (t/now)]
      (with-redefs [clj-time.core/now (fn [] now)]
        (let [card (cards/insert *conn* card-attributes)]
          (is (= (:created_at card) now))))))

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

  (testing "It has the correct cretion time"
    (let [card (cards/insert *conn* card-attributes)
          now (t/now)]
      (with-redefs [clj-time.core/now (fn [] now)]
        (let [updated-card (cards/update-by-id *conn* (:id card) {:title "New title"})]
          (is (= (:created_at updated-card) now))))))

  (testing "Fails if the attributes are invalid"
    (let [card (cards/insert *conn* card-attributes)
          updated-card (cards/update-by-id *conn* (:id card) {:title nil})]
      (is (some? (:errors updated-card))))))

(deftest delete-cards
  (let [card (cards/insert *conn* card-attributes)]
    (testing "Deletes the card having the given id"
      (cards/delete-by-id *conn* (:id card))
      (is (nil? (cards/find-by-id *conn* (:id card)))))))

(deftest search-cards-test
  (let [c1 (cards/insert *conn* {:tags "this-is-a-tag" :title "Clojure is a cool programming language", :contents "Any contents"})
        c2 (cards/insert *conn* {:tags "this-is-a-tag" :title "Annoying things", :contents "Cars and computers"})
        c3 (cards/insert *conn* {:tags "this-is-a-tag" :title "Programming languages" :contents "Clojure and Ruby are programming languages"})
        c4 (cards/insert *conn* {:tags "this-is-a-tag" :title "computers never work" :contents "But they are fun anyway"})
        c5 (cards/update-by-id *conn* (:id c3) {:title "Programming Languages"})]

    (testing "returns current cards that match the search term"
      (is (= (map :id (cards/search *conn* "programming")) [(:id c5) (:id c1)])))

    (testing "does not return ancestor cards"
      (is (not (some #{(:id c3)} (map :id (cards/search *conn* "programming")) ))))

    (testing "the title has a higher precedence than the contents in the results"
      (is (= (map :id (cards/search *conn* "computers")) [(:id c4) (:id c2)])))

    (testing "can search with multiple terms"
      (is (= (map :id (cards/search *conn* "programming languages")) [(:id c5) (:id c1)])))))
