(ns memoria.support.integration-test-helpers
  (:require [clojure.test :refer :all]
            [ring.adapter.jetty :refer  [run-jetty]]
            [slingshot.slingshot :refer [try+]]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [memoria.handlers.app :as app-handler]))

(def ^:private port-number 1234)
(def ^:private host (str "http://localhost:" port-number))

(defn setup-server-fixture []
  (use-fixtures
    :once
    (fn [f]
      (let [server (run-jetty #'app-handler/app {:port port-number :join? false})]
        (try
          (f)
          (finally
            (.stop server)))))))

(defn send-request-handling-errors [f]
  (try+
    (f)
    (catch [:status 404] {:keys [body headers]}
      {:body body :headers headers :status 404})
    (catch [:status 422] {:keys [body headers]}
      {:body body :headers headers :status 422})))

(defn- merge-headers [headers]
  (assoc (or headers {}) "Content-Type" "application/json" "memoria-mode" "test"))

(defn do-get
  ([url] (do-get url {}))
  ([url params] (do-get url params {}))
  ([url params headers]
   (let [headers (merge-headers headers)
         f #(http/get (str host url) {:query-params params :headers headers})
         response (send-request-handling-errors f)]
     (assoc response :body (json/read-str (:body response))))))

(defn do-post
  ([url] (do-post url {}))
  ([url body] (do-post url body {}))
  ([url body headers]
   (let [headers (merge-headers headers)
         f #(http/post (str host url) {:body (json/write-str body) :headers headers})
         response (send-request-handling-errors f)]
     (assoc response :body (json/read-str (:body response))))))

(defn do-patch
  ([url body] (do-patch url body {}))
  ([url body headers]
   (let [headers (merge-headers headers)
         f #(http/patch (str host url) {:body (json/write-str body) :headers headers})
         response (send-request-handling-errors f)]
     (assoc response :body (json/read-str (:body response))))))

(defn do-delete
  ([url] (do-delete url {}))
  ([url headers]
   (let [headers (merge-headers headers)
         f #(http/delete (str host url) {:body (json/write-str {}) :headers headers})
         response (send-request-handling-errors f)]
     (assoc response :body (json/read-str (:body response))))))
