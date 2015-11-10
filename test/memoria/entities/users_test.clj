(ns memoria.entities.users-test
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [memoria.entities.users :as users]
            [memoria.db :refer [*conn*]]
            [memoria.support.db-test-helpers :refer [setup-database-rollbacks]]
            [clojure.test :refer :all]))

(setup-database-rollbacks :transaction)

(def user-attributes {:google_id "12345"
                      :display_name "John Doe"
                      :email "john.doe@google.com"
                      :photo_url "http://example.com/johndoe.jpg"})

(deftest validations-test
  (let [user (users/validate {})]
    (testing "validates google_id presence"
      (is (= (get-in user [:errors :google_id])
             '("google_id must be present"))))

    (testing "validates display_name presence"
      (is (= (get-in user [:errors :display_name])
             '("display_name must be present"))))

    (testing "validates email presence"
      (is (= (get-in user [:errors :email])
             '("email must be present")))))

  (let [user (users/validate {:email "wrong"})]
    (testing "validates email format"
      (is (= (get-in user [:errors :email])
             '("email must be a valid email address"))))))

(deftest insert-valid-user-test
  (let [previous-count (users/cnt *conn*)
        user (users/insert *conn* user-attributes)]
    (testing "it increases the total users count"
      (is (= (users/cnt *conn*) (+ 1 previous-count))))

    (testing "the inserted user has the correct google_id"
      (is (= (:google_id user) (:google_id user-attributes))))

    (testing "the inserted user has the correct email address"
      (is (= (:email user) (:email user-attributes))))

    (testing "the inserted user has the correct display name"
      (is (= (:display_name user) (:display_name user-attributes))))

    (testing "the inserted user has the correct photo_url"
      (= (= (:photo_url user) (:photo_url user-attributes))))

    (testing "the inserted user has the creation time"
      (is (some? (:created_at user))))))

(deftest insert-invalid-user-test
  (let [previous-count (users/cnt *conn*)
        user (users/insert *conn* {})]
    (testing "does not change the number of existent users"
      (is (= previous-count (users/cnt *conn*))))

    (testing "returns an user with errors"
      (is (some? (:errors user))))))

(deftest inserting-a-duplicated-user
  (let [existent-user (users/insert *conn* user-attributes)
        previous-count (users/cnt *conn*)
        new-user (users/insert *conn* user-attributes)]
    (testing "returns the pre-existent user"
      (is (nil? (:errors new-user)))
      (is (= (:id existent-user) (:id new-user))))

    (testing "it does not add a new user to the database"
        (is (= previous-count (users/cnt *conn*))))))

(deftest find-by-google-id-test
  (let [user (users/insert *conn* user-attributes)]
    (testing "returns the user if there is a user with the given google_id"
      (is (:email (users/find-by-google-id *conn* (:google_id user-attributes)))
          (:email user-attributes)))))
