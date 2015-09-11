(ns memoria.formatting-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [memoria.formatting :refer [strip-images]]))

(deftest strip-images-test
  (testing "When the content has an image tag"
    (let [contents "Whatever\n\nBlabla\n![Image](http://foo/bar.jpg)"]
      (is (= (strip-images contents) "Whatever\n\nBlabla\n"))))

  (testing "When the content has multiple image tags"
    (let [contents "Whatever ![foo](http://foo/bar.jpg)\n![bar](http://whatever/foo.jpg)"]
      (is (= (strip-images contents) "Whatever \n"))))

  (testing "When the content does not have an image tag"
    (let [contents "Whatever\n\nMore content"]
      (is (= (strip-images contents) contents)))))
