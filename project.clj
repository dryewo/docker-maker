(defproject docker-maker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache License, Version 2.0"
            :url  "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.0.3"]
                 [prismatic/plumbing "0.5.2"]
                 [aysylu/loom "0.5.4"]]
  :main ^:skip-aot docker-maker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev     [{:source-paths ["dev"]
                        :repl-options {:init-ns user}
                        :plugins      [[lein-midje "3.2"]]
                        :dependencies [[org.clojure/tools.namespace "0.2.10"]
                                       [org.clojure/java.classpath "0.2.2"]
                                       [midje "1.8.2"]
                                       [criterium "0.4.3"]]}]})
