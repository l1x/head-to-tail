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
    ;external
    [clojure.tools.cli          :refer [parse-opts]             ]
    [clojure.string             :refer [split-lines lower-case] ]
    [clojure.edn                :as     edn                     ])
  (:import 
    [java.io File])
  (:gen-class))

;; Helpers 

; Reading a file (the safe way)
; the only problem if the input file is huge
; todo check size and refuse to read over 100k
(defn read-file
  "Returns {:ok string } or {:error...}"
  [^String file]
  (try
    (cond
      (.isFile (File. file))
        {:ok (slurp file) }                         ; if .isFile is true {:ok string}
      :else
        (throw (Exception. "Input is not a file"))) ;the input is not a file, throw exception
  (catch Exception e
    {:error "Exception" :fn "read-file" :exception (.getMessage e) }))) ; catch all exceptions

;Parsing a string to Clojure data structures the safe way
(defn parse-edn-string
  [s]
  (try
    {:ok (clojure.edn/read-string s)}
  (catch Exception e
    {:error "Exception" :fn "parse-config" :exception (.getMessage e)})))

;This function wraps the read-file and the parse-edn-string
;so that it only return {:ok ... } or {:error ...} 
(defn read-config 
  [file]
  (let 
    [ file-string (read-file file) ]
    (cond
      (contains? file-string :ok)
        ;this return the {:ok} or {:error} from parse-edn-string
        (parse-edn-string (file-string :ok))
      :else
        file-string)))

;; OPS

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

(defn h-2-t-rec [])

(defn head-to-tail
  [config]
  (let [  head (get-in config [:ok :words :head]) 
          dict (map lower-case (split-lines (:ok (read-file (get-in config [:ok :dict :file])))))
          tail (get-in config [:ok :words :tail])
          res  (atom ()) 
          skip-list (atom ())
          prev-words (atom ())]
    (println head tail)
    ;find all
    (loop [word head]
      (if (= word tail)
        (println "stop")
      (let [words (remove (set @skip-list) (find-all-words word dict))]
        ;(println "words: " words)
        (if (or (nil? word) (empty? words))
            (recur (rand-nth @prev-words))
        ;else
            (let [apad ""]
          (swap! res conj {word words})
          (reset! prev-words words)
          (swap! skip-list conj words)
          (swap! skip-list conj word)
          (swap! skip-list flatten)
          (swap! skip-list distinct)
          (println "res: " @res)
          ;(println"skip-list: " @skip-list)
          (recur (first words)))))))))

;; CLI

(defn exit [status msg]
  (println msg)
  (System/exit status))

(def cli-options
  [
    ["-f" "--config-file FILE" "Configuration file" :default "conf/app.edn"]
    ["-d" "--dictionary" "Initiate connections" :default "/usr/share/dict/words" ]
  ])

(defn -main [& args]
  ;same-named symbols to the map keys
  ;parse-opts returns -> {:options {:config-file "file/path"}, :arguments [print-config], :summary...}
  (let [  {:keys [options arguments errors summary]} (parse-opts args cli-options)
          ; options => {:config-file "file/path" :help true ...}
          config (read-config (:config-file options)) ]

    ; Handle help and error conditions
    (cond
      (or (empty? config) (:error config))
        (exit 1 (str "Config cannot be read or parsed..." "\n" config))
      errors
        (exit 1 (str "Incorrect options supplied... Exiting...")))

    ; Execute program with options
    (case (first arguments)
      "print-config"
        (println config)
      "head-to-tail"
        (println (head-to-tail config))
      ;default
        (exit 1 (println "Dead end")))))

;; END
