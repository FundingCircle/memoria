(ns memoria.cards-list-test
  (:require [cljs.test :refer-macros [deftest is testing are use-fixtures]]
            [reagent.core :as r]
            [dommy.core :refer-macros [sel sel1]]
            [memoria.cards-list :as cards-list]
            [memoria.state :as state]
            [memoria.reagent-test-helper :refer [with-mounted-component click fire!]]))

(deftest listing-cards-test
  (reset! state/cards-atom [{:id 1
                             :title "A card"
                             :contents "The contents"
                             :created_at "2015-09-19T23:58:01.846Z"
                             :tags "one,two,three"}])
  (with-mounted-component (cards-list/cards-list-component)
    (fn [c div]
      (let [card (sel1 :#card-thumbnail-1)
            title (sel1 card "h2")
            link (sel1 title "a")
            tags (sel1 card ".tags")
            contents (sel1 card ".card-contents")
            created-at (sel1 card ".created-at")]
        (is (= "A card" (.-textContent link)))
        (is (= "one,two,three" (.-textContent tags)))
        (is (= "The contents\n" (.-textContent contents)))
        (is (= "19/09/2015 23:58:01" (.-textContent created-at)))))))

