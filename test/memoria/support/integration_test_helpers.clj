(ns memoria.support.integration-test-helpers
  (:require [clojure.test :refer :all]
            [ring.adapter.jetty :refer  [run-jetty]]
            [slingshot.slingshot :refer [try+]]
            [clj-http.client :refer [post]]
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

(defn do-post [url body headers]
  (let [body (or body {})
        headers (or headers {})
        f (fn [] (post (str host url) {:body (json/write-str body) :headers headers}))
        response (send-request-handling-errors f)]
    (assoc response :body (json/read-str (:body response)))))

