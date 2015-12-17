(ns docker-maker.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [docker-maker.core :refer :all]
            [loom.graph :as g]))

(def TEST_DB
  {:scenarios
   {"clojure" {:dependencies ["java"]
               :snippet      "RUN echo 'Clojure' >> /log.log"}
    "java"    {:dependencies nil
               :snippet      "RUN echo 'Java' >> /log.log"}
    "scala"   {:dependencies ["java"]
               :snippet      "RUN echo 'Scala' >> /log.log"}
    "python"  {:dependencies ["magic"]
               :snippet      "RUN echo 'Python' >> /log.log"}
    "magic"   {:dependencies nil
               :snippet      "RUN echo 'Magic' >> /log.log"}
    "stups"   {:dependencies ["python"]
               :snippet      "RUN echo 'Stups' >> /log.log"}}})

(facts "about generate-dockerfile"
  (generate-dockerfile TEST_DB {:scenarios ["scala"]}) =>
  "FROM ubuntu
MAINTAINER docker-maker
RUN echo 'Java' >> /log.log
RUN echo 'Scala' >> /log.log
CMD [\"/bin/bash\"]"
  )

(facts "about dependency-graph"
  (dependency-graph (:scenarios TEST_DB))
  => (g/digraph ["clojure" "java"] ["scala" "java"] ["stups" "python"] ["python" "magic"]))

(facts "about sorted-dependencies"
  (let [full-dependency-graph (g/digraph [1 2] [2 3] [4 5] [5 6] [5 3])]
    (sorted-dependencies full-dependency-graph [2 5]) => [6 3 2 5]))

(facts "about concat-snippets"
  (concat-snippets (:scenarios TEST_DB) ["java" "scala"])
  => "RUN echo 'Java' >> /log.log\nRUN echo 'Scala' >> /log.log")
