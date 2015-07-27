(ns memoria.handlers.cards-test
  (:require [memoria.handlers.cards :as cards-handler]
            [memoria.entities.cards :as cards]
            [clojure.test :refer :all]
            [memoria.support.debugging :refer :all]
            [ring.mock.request :as mock]))

(def a-card {:id 123 :title "A Card" :contents "The contents"})

(deftest listing-cards
  (with-redefs [cards/all (fn [] [a-card])]
    (let [response (cards-handler/cards-routes (mock/request :get "/cards"))]
      (testing "Responds with 200"
        (is (= (:status response) 200)))

      (testing "Returns the cards"
        (is (= (:body response) [a-card])))

      (testing "Has json as the content type"
        (is (= (get-in response [:headers "Content-Type"]) "application/json"))))))

(deftest getting-a-card
  (with-redefs [cards/find-by-id (fn [id] a-card)]
    (let [response (cards-handler/cards-routes (mock/request :get (str "/cards/" (:id a-card))))]
      (testing "Responds with 200"
        (is (= (:status response) 200)))

      (testing "Returns the card"
        (is (= (:body response) a-card)))

      (testing "Has json as the content type"
        (is (= (get-in response [:headers "Content-Type"]) "application/json"))))))

(deftest getting-a-not-found-card
  (with-redefs [cards/find-by-id (fn [id] nil)]
    (let [response (cards-handler/cards-routes (mock/request :get "/cards/123"))]
      (testing "Responds with 404"
        (is (= (:status response) 404)))

      (testing "Returns a not found message"
        (is (= (get-in response [:body :message]) "Could not find a card with id 123"))))))
