(ns user
  (:require [my.clojure-game-geek.schema :as s]
            [com.walmartlabs.lacinia :as lacinia]))

(def schema (s/load-schema))

(defn q [query-string] (lacinia/execute schema query-string nil nil))

(comment
  (q "{ gameById(id: \"1234\") { id name summary }}"))

