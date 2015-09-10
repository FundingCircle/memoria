(ns memoria.tags-test
  (:require [clojure.test :refer :all]
            [memoria.tags :as tags]
            [selmer.parser :as parser]
            [selmer.filters :as filters]))

(def content-text
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce sollicitudin mollis euismod. Integer eget porttitor sem. Sed laoreet, velit bibendum viverra fringilla, tortor ipsum ullamcorper nibh, sit amet mollis dolor diam quis metus. Aliquam erat volutpat. Maecenas gravida quis elit eget faucibus. Fusce sodales nisi sit amet velit sollicitudin bibendum. Praesent ante dui, gravida in nisl vel, porttitor sem. Sed laoreet, velit bibendum viverra fringilla, tortor ipsum ullamcorper nibh, sit amet mollis dolor diam quis metus. Aliquam erat volutpat. Maecenas gravida quis elit eget faucibus.")

(def expected-text
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce sollicitudin mollis euismod. Integer eget porttitor sem. Sed laoreet, velit bibendum viverra fringilla, tortor ipsum ullamcorper nibh, sit amet mollis dolor diam quis metus. Aliquam erat volutpat. Maecenas gravida quis elit eget faucibus. Fusce sodales nisi sit amet velit sollicitudin bibendum. Praesent ante dui, gravida in nisl vel, p...")

(deftest returns-400-charaters-test
  (testing "content is filter"
  (let [content (parser/render "{{text|truncate}}" {:text content-text})]
    (is (= content expected-text)))))

