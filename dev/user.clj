(ns user
  (:require [clojure.repl :refer [apropos dir doc find-doc pst source]]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.test :as test]
            [schema.core :as s]
            [midje.repl :as midje]
            [criterium.core :as crit]
            [loom.graph :as g]
            [loom.alg :as ga]
            [loom.alg-generic :as gag]))

(defmacro spy [x]
  `(let [x# ~x]
     (println ~(str x) "=>" x#)
     x#))

(defn tests []
  (s/with-fn-validation
    (midje/load-facts '*)))
