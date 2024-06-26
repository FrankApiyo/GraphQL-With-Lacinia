(ns my.clojure-game-geek.schema
  "Contains custom resolvers and a function to provide the full schema."
  (:require [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [clojure.edn :as edn]))

(defn resolve-game-by-id
  [games-map _context args _value]
  (let [{:keys [id]} args] (get games-map id)))

(defn resolve-board-game-designers
  [designers-map _context _args board-game]
  (print "designers: " designers-map)
  (->> board-game
       :designers
       (map designers-map)))

(defn resolve-designer-games
  [games-map _context _args designer]
  (let [{:keys [id]} designer]
    (->> games-map
         vals
         (filter #(-> %
                      :designers
                      (contains? id))))))

(defn entity-map [data k] (reduce #(assoc %1 (:id %2) %2) {} (get data k)))

(defn resolver-map
  []
  (let [cgg-data (-> (io/resource "cgg-data.edn")
                     slurp
                     edn/read-string)
        games-map (entity-map cgg-data :games)
        designers-map (entity-map cgg-data :designers)]
    {:Query/gameById (partial resolve-game-by-id games-map),
     :BoardGame/designers (partial resolve-board-game-designers designers-map),
     :Designer/games (partial resolve-designer-games games-map)}))

(defn load-schema
  []
  (-> (io/resource "cgg-schema.edn")
      slurp
      edn/read-string
      (util/inject-resolvers (resolver-map))
      schema/compile))

