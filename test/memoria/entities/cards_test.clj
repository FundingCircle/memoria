(ns memoria.entities.cards-test
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [memoria.entities.cards :as cards]
            [memoria.entities.users :as users]
            [memoria.db :refer [*conn*]]
            [memoria.support.db-test-helpers :refer [setup-database-rollbacks]]
            [clojure.test :refer :all]))

(setup-database-rollbacks :transaction)

(def user-attributes {:google_id "12345"
                      :display_name "John Doe"
                      :email "john.doe@google.com"
                      :photo_url "http://example.com/johndoe.jpg"})

(def card-attributes {:title "A Card"
                      :contents "These are the card's contents"
                      :tags "tag1 tag2 tag3"})

(deftest validations
  (let [card (cards/validate {})]
    (testing "Validates title presence"
      (is (= (get-in card [:errors :title])
             '("title must be present"))))

    (testing "Validates contents presence"
      (is (= (get-in card [:errors :contents])
             '("contents must be present"))))))

(deftest finding-cards-by-id
  (let [user (users/insert *conn* user-attributes)
        card (cards/insert *conn* (assoc card-attributes :user_id (:id user)))]
    (testing "When there is a card with the given id"
      (is (some? (cards/find-by-id *conn* (:id card))))
      (is (= (:user_name (cards/find-by-id *conn* (:id card)))
             (:display_name user))))

    (let [deleted-card (cards/insert *conn* (assoc card-attributes :user_id (:id user)))
          _ (cards/delete-by-id *conn* (:id deleted-card))]
      (is (some? (cards/find-by-id *conn* (:id deleted-card) true))))

    (testing "When there is no card with the given id"
      (is (nil? (cards/find-by-id *conn* (+ 10 (:id card))))))))

(deftest listing-latest-cards
  (testing "Returns latest 10 cards when no number is specified"
    (dotimes [n 11] (cards/insert *conn* card-attributes))
    (is (= (count (cards/latest *conn*))
           10)))

  (testing "Does not return soft-deleted cards"
    (let [card (cards/insert *conn* card-attributes)
          _ (cards/delete-by-id *conn* (:id card))]
      (is (not (some #{(:id card)} (map :id (cards/latest *conn*)))))))

  (testing "Returns specified number of cards"
    (dotimes [n 3] (cards/insert *conn* card-attributes))
    (is (= (map #(:title %1) (cards/latest *conn* 1 2))
           ["A Card" "A Card"])))

  (testing "Only returns latest version of a card"
    (let [user (users/insert *conn* user-attributes)
          old-card (cards/insert *conn* {:title "Old title" :contents "old contents"})
          card (cards/insert *conn* (assoc card-attributes :user_id (:id user)))]
      (cards/update-by-id *conn* (:id card) {:title "New title" :contents "Updated contents" :user_id (:id user)})
      (cards/insert *conn* {:title "Another title" :contents "More contents"})
      (is (= (map #(:title %1) (cards/latest *conn* 1 3))
             ["Another title" "New title" "Old title"])))))

(deftest cards-insertion-test
  (testing "Inserting a new card increases the total cards count"
    (let [initial-count (cards/cnt *conn*)]
      (cards/insert *conn* card-attributes)
      (is (= (cards/cnt *conn*)
             (+ 1 initial-count)))))

  (testing "The inserted card is associated to the correct user"
    (let [user (users/insert *conn* user-attributes)
          card (cards/insert *conn* (assoc card-attributes :user_id (:id user)))]
      (is (= (:user_id card) (:id user)))))

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
  (let [user (users/insert *conn* user-attributes)
        card-attributes (assoc card-attributes :user_id (:id user))]
    (testing "It does not update the database record for the card"
      (let [card (cards/insert *conn* card-attributes)
            updated-card (cards/update-by-id *conn* (:id card) {:title "New title" :contents "New contents" :user_id (:id user)})
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

    (testing "Associates the user to the updated card"
      (let [card (cards/insert *conn* card-attributes)
            new-user (users/insert *conn* user-attributes)
            updated-card (cards/update-by-id *conn* (:id card) {:contents "New contents" :user_id (:id new-user)})]
        (is (= (:user_id updated-card) (:id new-user)))))

    (testing "Fails if the attributes are invalid"
      (let [card (cards/insert *conn* card-attributes)
            updated-card (cards/update-by-id *conn* (:id card) {:title nil})]
        (is (some? (:errors updated-card)))))))

(deftest delete-cards
  (let [user (users/insert *conn* user-attributes)
        card-attributes (assoc card-attributes :user_id (:id user))
        card (cards/insert *conn* card-attributes)]
    (testing "Marks the card as deleted"
      (cards/delete-by-id *conn* (:id card))
      (is (nil? (cards/find-by-id *conn* (:id card))))
      (is (some? (cards/find-by-id *conn* (:id card) true))))))

(deftest search-cards-test
  (let [user (users/insert *conn* user-attributes)
        c1 (cards/insert *conn* {:tags "this-is-a-tag" :title "Clojure is a cool programming language", :contents "Any contents" :user_id (:id user)})
        c2 (cards/insert *conn* {:tags "this-is-a-tag" :title "Annoying things", :contents "Cars and computers" :user_id (:id user)})
        c3 (cards/insert *conn* {:tags "this-is-a-tag" :title "Programming languages" :contents "Clojure and Ruby are programming languages" :user_id (:id user)})
        c4 (cards/insert *conn* {:tags "this-is-a-tag" :title "computers never work" :contents "But they are fun anyway" :user_id (:id user)})
        c5 (cards/update-by-id *conn* (:id c3) {:title "Programming Languages" :user_id (:id user)})]

    (testing "returns current cards that match the search term"
      (is (= (map :id (cards/search *conn* "programming")) [(:id c5) (:id c1)])))

    (testing "does not return ancestor cards"
      (is (not (some #{(:id c3)} (map :id (cards/search *conn* "programming")) ))))

    (testing "the title has a higher precedence than the contents in the results"
      (is (= (map :id (cards/search *conn* "computers")) [(:id c4) (:id c2)])))

    (testing "can search with multiple terms"
      (is (= (map :id (cards/search *conn* "programming languages")) [(:id c5) (:id c1)])))))

(deftest search-deleted-cards
  (let [c (cards/insert *conn* card-attributes)]
    (testing "does not return deleted cards"
      (cards/delete-by-id *conn* (:id c)))
    (is (empty? (cards/search *conn* (:title c))))))
