(ns memoria.data-binding)

(defn bind-input [input-atom]
  #(reset! input-atom (-> %1 .-target .-value)))


