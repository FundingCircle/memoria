(ns memoria.ajax
  (:require [ajax.core :as a]))

(defn- error-handler [{:keys [status status-text]}]
  (.log js/console
        (str "Something bad happened: " status " " status-text)))

(defn do-get
  ([path handler] (do-get path handler {}))
  ([path handler params] (a/GET path {:headers {"Content-Type" "application/json"}
                              :params params
                              :format :json
                              :response-format :json
                              :keywords? true
                              :prefix "while(1);"
                              :handler handler
                              :error-handler error-handler})))

(defn do-post [path handler params]
  (a/POST path {:headers {"Content-Type" "application/json"}
                :params params
                :format :json
                :response-format :json
                :keywords? true
                :handler handler
                :error-handler error-handler}))

