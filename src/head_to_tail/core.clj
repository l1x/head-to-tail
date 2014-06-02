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
    [clojure.set        :refer  [intersection]            ]
    [loom.graph         :refer  :all                      ]
    [loom.io            :refer  :all                      ]
    [loom.alg           :refer  :all                      ]
    [clojure.string     :refer  [split-lines lower-case]  ])
  (:gen-class))

(defn- word-to-patterns
  "This must use position based replace"
  [word] 
  (map re-pattern 
    (for [char (range (count word))]  (str "\\b" (subs word 0 char) "." (subs word (+ char 1) ) "\\b"))))
 
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

(defn dict 
  [file] 
  (map lower-case (split-lines (:ok (read-file file)))))

(defn head-to-tail
  [config]
  (let [  head        (get-in config [:ok :words :head])
          tail        (get-in config [:ok :words :tail])
          dict        (filter #(= (count head) (count %)) (dict (get-in config [:ok :dict :file])))
          skip-list   (atom ())
          prev-words  (atom ()) 
          adj         (atom {}) ]
    ;ops
    (doseq [word dict] 
      (let [skip-list (conj (filter (comp #{word} @adj) (keys @adj)) word)]
      (swap! adj assoc-in [word] (remove (set skip-list) (find-all-words word dict)))))
      (println @adj)
      (let [g (graph @adj)]
        (println (bf-path g head tail)))))




