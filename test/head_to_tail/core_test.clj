(ns head-to-tail.core-test
  (:require [clojure.test :refer :all]
            [head-to-tail.core :refer :all]))

(defn head-to-tail-pairs [] [
  [ {:ok {:words {:head "head", :tail "tail"}, :dict {:file "data/wordsEn.txt"}}}
    (list "head" "heal" "heil" "hail" "tail") ]
])

(deftest head-to-tail-test
  (testing "head -> tail"
    (doseq
      [pair (head-to-tail-pairs)]
      (is
        (=
          (head-to-tail (nth pair 0)) (nth pair 1))))))

