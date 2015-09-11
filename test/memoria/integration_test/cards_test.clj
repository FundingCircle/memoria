(ns memoria.integration-test.cards-test
  (:require [memoria.support.db-test-helpers :refer [setup-database-rollbacks]]
            [memoria.support.integration-test-helpers :refer :all]
            [memoria.db :refer [*conn*]]
            [memoria.entities.cards :as cards]
            [clojure.test :refer :all]))

(setup-database-rollbacks :truncation)
(setup-server-fixture)

(deftest listing-latest-cards
  (let [cards (cards/insert *conn* {:title "First Card" :contents "First contents"})
        more-cards (dotimes [n 11] (cards/insert *conn* {:title "A Card" :contents "These are the contents"}))]
    (testing "It succeeds"
      (let [response (do-get "/cards" {:page "2"})
            {:keys [status body headers]} response]
        (is (= (count body) 2))
        (is (= (headers "content-type") "application/json; charset=utf-8"))
        (is (= status 200))
        (is (= (get (first body) "title") "A Card"))
        (is (= (get (second body) "title") "First Card"))))))

(deftest getting-a-card-by-id
  (let [card (cards/insert *conn* {:title "A Card" :contents "These are the contents"})]
    (testing "It succeeds"
      (let [response (do-get (str "/cards/" (:id card)))
            {:keys [status body headers]} response]
        (is (= status 200))
        (is (= (headers "content-type") "application/json; charset=utf-8"))
        (is (= (get body "title") "A Card"))
        (is (= (get body "contents") "These are the contents"))))))

(deftest creating-a-card-with-valid-attributes
  (testing "It succeeds"
    (let [attrs {:title "This is a card" :contents "These are the card's contents"}
          response (do-post "/cards" attrs)
          {:keys [status body headers]} response]
      (is (= (get body "title") "This is a card"))
      (is (some? (get body "id")))
      (is (= (:errors response) nil))
      (is (= status 201)))))

(deftest creating-a-card-with-invalid-attributes
  (testing "It fails"
    (let [attrs {}
          response (do-post "/cards" attrs)
          status (:status response)
          body (:body response)]
      (is (nil? (get body "title")))
      (is (= (get-in body ["errors" "title"]) ["title must be present"]))
      (is (= status 422)))))

(deftest updating-a-card-with-valid-attributes
  (let [card (cards/insert *conn* {:title "This is a card" :contents "These are the contents"})]
    (testing "It succeeds"
      (let [attrs {:title "This is the new title" :contents "New contents"}
            response (do-post (str "/cards/" (:id card)) attrs)
            {:keys [status body headers]} response
            updated-card (cards/find-by-id *conn* (get body "id"))]
        (is (= status 200))
        (is (= (:title updated-card) "This is the new title"))
        (is (= (:contents updated-card) "New contents"))
        (is (= (get headers "content-type") "application/json; charset=utf-8"))))))

(deftest udpating-a-card-with-invalid-attributes
  (let [card (cards/insert *conn* {:title "This is a card" :contents "These are the contents"})]
    (testing "It fails"
      (let [attrs {:title nil :contents nil}
            response (do-post (str "/cards/" (:id card)) attrs)
            {:keys [status body headers]} response]
        (is (= status) 422)
        (is (nil? (get body "title")))
        (is (= (get-in body ["errors" "title"]) ["title must be present"]))))))

(deftest deleting-a-card
  (let [card (cards/insert *conn* {:title "This is a card" :contents "These are the contents"})]
    (testing "It succeeds"
      (let [response (do-delete (str "/cards/" (:id card)))
            {:keys [status body headers]} response
            reloaded-card (cards/find-by-id *conn* (:id card))]
        (is (= status 200))
        (is (= body {}))
        (is (nil? reloaded-card))))))

(deftest searching-cards
  (let [pretty-card (cards/insert *conn* {:title "This is a pretty card" :contents "These are pretty contents"})
        another-pretty-card (cards/insert *conn* {:title "Another pretty card" :contents "More prettiness"})
        ugly-card (cards/insert *conn* {:title "This is an ugly card" :contents "These are ugly contents"})
        more-cards (dotimes [n 9] (cards/insert *conn* {:title "Another card" :contents "Not quite as pretty"}))
        latest-card (cards/insert *conn* {:title "Yet another card" :contents "Not quite as pretty"})]
    (testing "It returns only cards the match the search term"
      (let [response (do-get "/search-cards" {:q "pretty" :page "2"})
            {:keys [status body headers]} response]
        (is (= status 200))
        (is (= (count body) 2))
        (is (= (get (first body) "title") "Another card"))
        (is (= (get (second body) "title") "Yet another card"))))

    (testing "It returns the latest cards when the search term is empty"
      (let [response (do-get "/search-cards" {})
            {:keys [status body headers]} response]
        (is (= status 200))
        (is (= (count body) 10))))))
