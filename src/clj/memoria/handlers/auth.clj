(ns memoria.handlers.auth
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.walk :refer [keywordize-keys]]
            [ring.middleware.json :refer [wrap-json-response]]
            [memoria.db :as db]
            [memoria.entities.users :as users]))

(defn- user-details->user-attrs
  [user-details]
  (let [user-details (keywordize-keys user-details)]
    {:google_id (:id user-details)
     :display_name (:displayName user-details)
     :email (-> user-details :emails first :value)
     :photo_url (get-in user-details [:image :url])}))

(defn register-user-details
  [body]
  (let [attrs (user-details->user-attrs body)
        user (users/insert db/*conn* attrs)]
    {:status 201
     :body user}))

(defroutes user-auth-routes
  (POST "/auth" {body :body :as req} (register-user-details body)))
