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
    [clojure.string     :refer  [split-lines lower-case]  ])
  (:gen-class))

(defn- word-to-patterns
  "This must use position based replace"
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

(defn dict 
  [file] 
  (map lower-case (split-lines (:ok (read-file file)))))

(defn fast-path-words 
  " Takes 2 words and a dictionary as the input 
    and return maximum count(word) words that are English words
    by trying to replace 1 letter in the first word from the 
    corresponding letter from the second word
    example: head tail dict -> (heal)  "
  ^clojure.lang.PersistentList [^String head ^String tail ^clojure.lang.PersistentList dict]
  (into () 
    (intersection 
      (set dict) 
      (set (for [c (range (count head))]  (str (subs head 0 c) (nth tail c) (subs head (+ c 1) )))))))

(defn head-to-tail
  [config]
  (let [  head (get-in config [:ok :words :head]) 
          dict (filter #(= (count head) (count %)) (dict (get-in config [:ok :dict :file])))
          tail (get-in config [:ok :words :tail])
          res  (atom {}) 
          skip-list (atom ())
          prev-words (atom ()) ]

;{head (heal)}
;{head (heal), heal (teal heil)}
;{head (heal), heal (teal heil), heil (hail)}
;{head (heal), heal (teal heil), heil (hail), hail (tail)}

(loop [word head]
  (let [  skip-list (conj (filter (comp #{word} @res) (keys @res)) word)
          words-fp  (remove (set skip-list) (fast-path-words word tail dict))
          words-rp  (remove (set skip-list) (find-all-words word dict))
          words     (cond (not (empty? words-fp)) words-fp :else words-rp) ]

    (println words)
    (swap! res assoc-in [word] words)
    (println @res)
    (if (contains? (set words) tail)
      "stop"
    ;else
      (recur (rand-nth words)))))))

; (defstruct tree :val :w0 :w1 :w2 :w3)

; (defn bftrav [& trees]
;   (when trees
;     (concat trees 
;       (->> trees
;       (mapcat (juxt :w0 :w1 :w2 :w3))
;       (filter identity)
;       (apply bftrav)))))

; (def my-tree 
;   (struct tree "head"
;     (struct tree "tead")
;     (struct tree "haad")
;     (struct tree "heid")
;     (struct tree "heal"
;       (struct tree "teal"
;         (struct tree "taal") 
;         (struct tree "teil"))
;       (struct tree "haal")
;       (struct tree "hail"
;         (struct tree "tail")))))

; (bftrav my-tree)
