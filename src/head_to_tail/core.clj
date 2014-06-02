;;Copyright 2014 Istvan Szukacs

;;Licensed under the Apache License, Version 2.0 (the "License");
;;you may not use this file except in compliance with the License.
;;You may obtain a copy of the License at

;;    http://www.apache.org/licenses/LICENSE-2.0

;;Unless required by applicable law or agreed to in writing, software
;;distributed under the License is distributed on an "AS IS" BASIS,
;;WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;;See the License for the specific language governing permissions and
;;limitations under the License
(ns head-to-tail.core
  (:require
    ;internal
    [head-to-tail.helpers :refer [read-file]              ]
    ;external
    [clojure.data.json  :as     json                      ]
    [clojure.string     :refer  [split-lines lower-case]  ])
  (:gen-class))

(defn- word-to-patterns
  [w] 
  (map re-pattern 
    (for [c w] 
      (str "\\b" (clojure.string/replace w (str c) ".") "\\b"))))
 
(defn- find-words
  [p dic] 
  (filter #(re-find p %) dic))
 
(defn- find-all-words
  [word dic]
  (remove #{word}
    (distinct 
      (flatten 
        (for 
          [p (word-to-patterns word)] 
          (find-words p dic))))))

;(defn find-replacement 
;  [word char pos]
;  (let [dict (dict (get-in config [:ok :dict :file]))]
;  
;  )

(defn dict [file] (map lower-case (split-lines (:ok (read-file file)))))

(defn head-to-tail
  [config]
  (let [  head (get-in config [:ok :words :head]) 
          dict (filter #(= (count head) (count %)) (dict (get-in config [:ok :dict :file])))
          tail (get-in config [:ok :words :tail])
          res  (atom {}) 
          skip-list (atom ())
          prev-words (atom ()) ]
(println (count dict))
; look up all the words that are different in one letter
; remove the parent words from this list
; recur with the random element of this list

; 0:
;   lookup: head -> dead heal
;   skip-list: ()
;   {"head" ("dead" "heal"....)}

; 1:
;   lookup: dead -> head lead deal
;   skip-list: ("head")
;   {"dead"} -> ("lead"...)

(loop [word head]
  (println word)
  (let [  skip-list (filter (comp #{word} @res) (keys @res))
          words (remove (set skip-list) (find-all-words word dict)) ]
    (swap! res assoc-in [word] words)
    (println (keys @res))
    (if (contains? (set words) tail)
      "stop"
    ;else
      (do (println (count words))
      (recur (rand-nth words))))))))

    

