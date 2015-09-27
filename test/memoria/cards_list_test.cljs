(ns memoria.cards-list-test
  (:require [cljs.test :refer-macros [deftest is testing are use-fixtures]]
            [reagent.core :as r]
            [dommy.core :refer-macros [sel sel1]]
            [memoria.cards-list :as cards-list]
            [memoria.cards-state :as state]))

(def isClient (not (nil? (try (.-document js/window)
                              (catch js/Object e nil)))))

(defn add-test-div [name]
  (let [doc     js/document
        body    (.-body js/document)
        div     (.createElement doc "div")]
    (.appendChild body div)
    div))

(defn with-mounted-component [comp f]
  (when isClient
    (let [div (add-test-div "_testreagent")]
      (let [comp (r/render-component comp div #(f comp div))]
        (r/unmount-component-at-node div)
        (r/flush)
        (.removeChild (.-body js/document) div)))))

(defn found-in [re div]
  (let [res (.-innerHTML div)]
    (if (re-find re res)
      true
      (do (println "Not found: " res)
          false))))

(defn click
  [el]
  (let [ev (.createEvent js/document "MouseEvent")]
    (.initMouseEvent ev "click" true js/window nil 0 0 0 0 false false false false 0 nil)
    (.dispatchEvent el ev)))

(defn fire!
  "Creates an event of type `event-type`, optionally having
     `update-event!` mutate and return an updated event object,
     and fires it on `node`.
     Only works when `node` is in the DOM"
  [node event-type & [update-event!]]
  (let [update-event! (or update-event! identity)]
    (if (.-createEvent js/document)
      (let [event (.createEvent js/document "Event")]
        (.initEvent event (name event-type) true true)
        (.dispatchEvent node (update-event! event)))
      (.fireEvent node (str "on" (name event-type))
                  (update-event! (.createEventObject js/document))))))

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

