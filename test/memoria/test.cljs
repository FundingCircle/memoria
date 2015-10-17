(ns memoria.test
  (:require [memoria.app-test]
            [memoria.formatting-test]
            [memoria.cards-list-test]
            [memoria.app-test]
            [cljs.test :refer-macros [run-all-tests]]))

(enable-console-print!)

(defn ^:export run
  []
  (run-all-tests #"memoria.*test"))
