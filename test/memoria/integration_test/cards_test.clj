(ns memoria.integration-test.cards-test
  (:require [memoria.support.db-test-helpers :as db-helpers]
            [memoria.support.integration-test-helpers :refer :all]
            [clojure.test :refer :all]))

(db-helpers/setup-db-test :truncation)
(setup-server-fixture)

(deftest creating-a-card-with-valid-attributes
  (testing "It succeeds"
    (let [attrs {:title "This is a card" :contents "These are the card's contents"}
          response (do-post "/cards" attrs {"Content-Type" "application/json"})
          status (:status response)
          body (:body response)]
      (is (= (get body "title") "This is a card"))
      (is (some? (get body "id")))
      (is (= (:errors response) nil))
      (is (= status 201)))))

(deftest creating-a-card-with-invalid-attributes
  (testing "It fails"
    (let [attrs {}
          response (do-post "/cards" attrs {"Content-Type" "application/json"})
          status (:status response)
          body (:body response)]
      (is (nil? (get body "title")))
      (is (= (get-in body ["errors" "title"]) ["title must be present"]))
      (is (= status 422)))))
