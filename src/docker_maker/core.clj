(ns docker-maker.core
  (:gen-class)
  (:require [schema.core :as s]
            [plumbing.core :as p]
            [loom.graph :as g]
            [loom.alg :as ga]
            [clojure.string :as str]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(defn dependency-graph
  "Build a dependency digraph out of the scenario database"
  [scenarios-db]
  (g/digraph (p/map-vals :dependencies scenarios-db)))

;; TODO implement multi-traverse (strating with a set of nodes instead of one)

(defn sorted-dependencies
  "Given a full dependency graph and a list of required nodes,
  builds a topologically sorted list of nodes with all necessary dependencies."
  [dependency-graph nodes]
  (->> nodes
       (mapcat (partial ga/pre-traverse dependency-graph))  ; find reachable nodes for each of the starting ones
       set                                                  ; remove duplicates
       (g/subgraph dependency-graph)                        ; narrow down the dependency graph
       (ga/topsort)                                         ; topologically sort
       reverse))                                            ; root dependencies first

(defn concat-snippets
  "Given a database and a list of required scenarios extracts and concatenates the corresponding snippets together"
  [scenarios-db required-scenarios]
  (->> required-scenarios
       (map #(-> scenarios-db (get %) :snippet))
       (str/join "\n")))

(def HEADER "FROM ubuntu\nMAINTAINER docker-maker")

(def FOOTER "CMD [\"/bin/bash\"]")

(defn generate-dockerfile
  "Generates a Dockerfile based on the database and provided spec"
  [db spec]
  (let [required-scenarios    (:scenarios spec)
        full-dependency-graph (dependency-graph (:scenarios db))
        full-scenario-list    (sorted-dependencies full-dependency-graph required-scenarios)
        concatenated-snippets (concat-snippets (:scenarios db) full-scenario-list)]
    (str/join "\n" [HEADER concatenated-snippets FOOTER])))

;(defn build-image [spec]
;  )
