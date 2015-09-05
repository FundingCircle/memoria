(ns memoria.core
            [memoria.config :as config]
            [memoria.handlers.app :as app-handlers]))


(def app app-handlers/app)

