(ns memoria.app-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [dommy.core :refer-macros [sel sel1] :refer [attr]]
            [memoria.app :as app]
            [memoria.state :refer [user-details]]
            [memoria.reagent-test-helper :refer [with-mounted-component click fire!]]))

(deftest banner-component-test
  (testing "with an authenticated user"
    (reset! user-details {:display_name "John Doe"
                          :photo_url "/foo/bar.jpg"})
    (with-mounted-component (app/banner-component)
      (fn [c div]
        (let [user-name (sel1 ".user-name")
              user-photo (sel1 ".user-photo img")]
          (is (= "John Doe" (.-textContent user-name)))
          (is (= "/foo/bar.jpg" (attr user-photo "src")))))))

  (testing "with a non authenticated user"
    (reset! user-details nil)
    (with-mounted-component (app/banner-component)
      (fn [c div]
        (let [auth-button (sel1 "#auth-button .button-text")]
          (is (= "Sign in with Google") (.-textContent auth-button)))))))
