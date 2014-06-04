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
(ns head-to-tail.helpers
  (:require
    ;internal
    ;external
    [clojure.edn :as edn ])
  (:import
    [java.io File])
  (:gen-class))

(defn read-file
  "Returns {:ok string } or {:error...}"
  [^File file]
  (try
    (cond
      (.isFile file)
        {:ok (slurp file) }                         ; if .isFile is true {:ok string}
      :else
        (throw (Exception. "Input is not a file"))) ;the input is not a file, throw exception
  (catch Exception e
    {:error "Exception" :fn "read-file" :exception (.getMessage e) }))) ; catch all exceptions

(defn save-file
  "Returns {:ok string } or {:error...}"
  [^File file content]
    (try
      {:ok (spit file content)}
    (catch Exception e
      {:error "Exception" :fn "save-file" :exception (.getMessage e)})))

;Parsing a string to Clojure data structures the safe way
(defn parse-edn-string
  [s]
  (try
    {:ok (edn/read-string s)}
  (catch Exception e
    {:error "Exception" :fn "parse-config" :exception (.getMessage e)})))

;This function wraps the read-file and the parse-edn-string
;so that it only return {:ok ... } or {:error ...}
(defn read-config
  [path]
  (let
    [ file-string (read-file (File. path)) ]
    (cond
      (contains? file-string :ok)
        ;this return the {:ok} or {:error} from parse-edn-string
        (parse-edn-string (file-string :ok))
      :else
        file-string)))
