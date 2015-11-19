(ns memoria.entities.users
  (:require [clj-time.jdbc]
            [clj-time.core :as t]
            [yesql.core :refer [defqueries]]
            [bouncer.validators :as v]
            [memoria.entities.validations :refer [validate-entity valid?]]))

(defqueries "sql/users.sql")

(defn validate [user]
  (validate-entity user [:google_id v/required
                         :display_name v/required
                         :email [v/required v/email]]))

(defn cnt
  "Returns the total number of users in the database"
  [db]
  (:count (first (count-users {} {:connection db}))))

(defn find-by-google-id
  "Finds a user by google-id.

  Returns nil if no user can be found."
  [db google-id]
  (first (find-user-by-google-id {:google_id google-id} {:connection db})))

(defn- find-or-insert-user
  [db attrs]
  (if-let [user (find-by-google-id db (:google_id attrs))]
    user
    (insert-user<! (assoc attrs :created_at (t/now)) {:connection db})))

(defn insert
  "Validates the received attributes and check if the user
  has been registered before.

  If there are errors, the received attributes are returned,
  with the validation errors.

  If the user has been registered before, the existent record
  is returned. Otherwise, a new record is inserted and returned."
  [db attrs]
  (let [attrs (validate attrs)]
    (if (valid? attrs)
      (find-or-insert-user db attrs)
      attrs)))
