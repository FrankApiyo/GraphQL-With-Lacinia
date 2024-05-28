(ns user
  (:require [my.clojure-game-geek.schema :as s]
            [com.walmartlabs.lacinia :as lacinia]
            [com.walmartlabs.lacinia.pedestal2 :as lp]
            [io.pedestal.http :as http]
            [clojure.java.browse :refer [browse-url]]))

(def schema (s/load-schema))

(defn q [query-string] (lacinia/execute schema query-string nil nil))

(comment
  (q
    "{ gameById(id: \"1234\") { id name designers { name url games { name }}}}"))

(defonce server nil)

(defn start-server
  [_]
  (let [server (-> (lp/default-service schema nil)
                   http/create-server
                   http/start)]
    (browse-url "http://localhost:8888/ide")
    server))

(defn stop-server
  [server]
  (http/stop server)
  nil)

(defn start
  []
  (alter-var-root #'server start-server)
  :started)

(defn stop
  []
  (alter-var-root #'server stop-server)
  :stopped)

(comment
  (start)
  (stop))

