(ns memoria.handlers.cards-test
  (:require [memoria.handlers.cards :as cards-handler]
            [memoria.entities.cards :as cards]
            [clojure.test :refer :all]
            [memoria.support.debugging :refer :all]
            [ring.mock.request :as mock]))

(def a-card {:id 123 :title "A Card" :contents "The contents"})

(deftest listing-cards
  (with-redefs [cards/latest (constantly [a-card])]
    (let [response (cards-handler/cards-routes (mock/request :get "/cards"))]
      (testing "Responds with 200"
        (is (= (:status response) 200)))

      (testing "Returns the cards"
        (is (= (:body response) [a-card]))))))

(deftest getting-a-card
  (with-redefs [cards/find-by-id (constantly a-card)]
    (let [response (cards-handler/cards-routes (mock/request :get (str "/cards/" (:id a-card))))]
      (testing "Responds with 200"
        (is (= (:status response) 200)))

      (testing "Returns the card"
        (is (= (:body response) a-card))))))

(deftest getting-a-not-found-card
  (with-redefs [cards/find-by-id (constantly nil)]
    (let [response (cards-handler/cards-routes (mock/request :get "/cards/123"))]
      (testing "Responds with 404"
        (is (= (:status response) 404)))

      (testing "Returns a not found message"
        (is (= (get-in response [:body :message])
               "Could not find a card with id 123"))))))

(deftest inserting-a-card
  (with-redefs [cards/insert (constantly a-card)]
    (let [attrs (select-keys a-card [:title :contents])
          response (cards-handler/cards-routes (mock/request :post "/cards" attrs))]
      (testing "Responds with 200"
        (is (= (:status response) 201)))

      (testing "Returns the created card"
        (is (= (get-in response [:body :id]) 123))))))

(deftest inserting-a-card-with-invalid-attributes
  (with-redefs [cards/insert (constantly (assoc a-card :errors {:title "Can't be blank."}))]
    (let [attrs {}
          response (cards-handler/cards-routes (mock/request :post "/cards" attrs))]
      (testing "Responds with 422"
        (is (= (:status response) 422)))

      (testing "Returns the created card"
        (is (= (get-in response [:body :id]) 123))))))

(deftest updating-a-card
  (with-redefs [cards/update-by-id (constantly (assoc a-card :title "New title"))]
    (let [attrs {:title "New title"}
          response (cards-handler/cards-routes (mock/request :post (str "/cards/" (:id a-card))))]
      (testing "Responds with 200"
        (is (= (:status response) 200)))

      (testing "Returns the updated card"
        (is (= (get-in response [:body :title]) "New title"))))))
