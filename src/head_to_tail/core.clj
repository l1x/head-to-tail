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
    [head-to-tail.helpers :refer [read-file save-file 
                                  parse-edn-string]       ]
    ;external
    [clojure.data.json  :as     json                      ]
    [clojure.set        :refer  [intersection]            ]
    [loom.graph         :refer  :all                      ]
    [loom.io            :refer  :all                      ]
    [loom.alg           :refer  :all                      ]
    [clojure.string     :refer  [split-lines lower-case]  ])
  (:import
    [java.io File])
  (:gen-class))

(defn- word-to-patterns
  "This must use position based replace"
  [word] 
  (map re-pattern 
    (for 
      [char (range (count word))]  
      (str "\\b" (subs word 0 char) "." (subs word (+ char 1) ) "\\b"))))
 
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
  (map lower-case (split-lines (:ok (read-file (File. file))))))

(defn gen-adjacency-list [dict]
  (let [adj (atom {})]
    (doseq [word dict]
      (println "word: " word)
      (swap! adj assoc-in [word] (find-all-words word dict)))
    ;return
    @adj))

(defn save-adjacency-list 
  [adj length] 
  (save-file (File. "data" (str length ".adj.edn")) adj))

(defn read-adjacency-list 
  [length] 
  (read-file (File. "data" (str length ".adj.edn"))))

(defn breadth-first-path 
  [graph head tail] 
  (bf-path graph head tail))

(defn head-to-tail
  [config]
  (let [  head        (get-in config [:ok :words :head])
          tail        (get-in config [:ok :words :tail])
          word-size   (count head)
          dict        (filter #(= word-size (count %)) 
                        (dict (get-in config [:ok :dict :file])))
          adj-list    (read-adjacency-list (count head)) ]
    ;ops
    (cond
      (contains? adj-list :ok)
        (do 
          (println "Saved adjacency-list is found...")
          (println "The shortest path: " 
            (breadth-first-path 
              (graph (:ok (parse-edn-string (:ok adj-list)))) head tail)))
      :else
        (let 
          [adj-list-new (gen-adjacency-list dict)]
          (println "Saving new adjacency-list...")
          (cond 
            (contains? (save-adjacency-list adj-list-new word-size) :ok)
              (do
                (println "Saving new adjacency-list is successful")
                (println "The shortest path: " 
                  (breadth-first-path (graph adj-list-new) head tail)))
            :else
              (println "Saving new adjacency-list has failed..."))))))
