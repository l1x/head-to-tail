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

(ns head-to-tail.cli
  (:require
    ;internal
    [head-to-tail.helpers :refer [read-config]  ]
    [head-to-tail.core    :refer [head-to-tail]  ]
    ;external
    [clojure.tools.cli    :refer [parse-opts]   ])
  (:gen-class))

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
        (head-to-tail config)
      ;default
        (exit 1 (println "Dead end")))))
