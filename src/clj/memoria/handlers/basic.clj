(ns memoria.handlers.basic)

(defn not-found-response [msg]
  {:status 404
   :body {:message msg}
   :headers {"Content-Type" "application/json"}})
