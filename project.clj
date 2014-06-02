(defproject head-to-tail "0.0.2"
  :description ""
  :url ""
  :license {:name " Apache License Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [
    [org.clojure/clojure          "1.6.0"]
    [org.clojure/core.async       "0.1.303.0-886421-alpha"]
    [org.clojure/tools.cli        "0.3.1"]
    [org.clojure/tools.logging    "0.2.6"]
    [org.clojure/data.json "0.2.4"]
    [com.google.guava/guava       "16.0" ]
    [aysylu/loom                  "0.4.2"]
    [org.clojure/data.json        "0.2.4"]]
  :exclusions [
  ]
  :profiles {
    :uberjar {
      :aot :all
    }
  }
  :jvm-opts [
    "-Xms256m" "-Xmx512m" "-server" "-XX:MaxPermSize=128m"
    "-XX:NewRatio=2" "-XX:+UseConcMarkSweepGC"
    "-XX:+TieredCompilation" "-XX:+AggressiveOpts"
    "-Dcom.sun.management.jmxremote"
    "-Dcom.sun.management.jmxremote.local.only=false"
    "-Dcom.sun.management.jmxremote.authenticate=false"
    "-Dcom.sun.management.jmxremote.ssl=false"
    ;"-Xprof" "-Xrunhprof"
  ]
  :main head-to-tail.cli)
