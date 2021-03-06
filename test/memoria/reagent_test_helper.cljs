(ns memoria.reagent-test-helper
    (:require [reagent.core :as r]))

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
