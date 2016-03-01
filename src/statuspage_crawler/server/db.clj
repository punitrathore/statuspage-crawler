(ns ^{:doc "CRU(D) functions for Jobs"}
  statuspage-crawler.server.db
  (:require [clojure.java.jdbc :as jdbc]
            [cheshire.core :as json]
            [statuspage-crawler.config :as config])
  (:import [org.postgresql.util PGobject]
           [java.util UUID]))

(defn ->uuid [^String s]
  (when s
    (UUID/fromString s)))

(defn create-new-job []
  (jdbc/with-db-transaction [txn (config/db-spec)]
    (first (jdbc/insert! txn :jobs {:status "inprogress"}))))

(defn update-job [^UUID id job]
  (jdbc/with-db-transaction [txn (config/db-spec)]
    (jdbc/update! txn :jobs job ["id=(?)" id])))

(defn find-job [id]
  (jdbc/with-db-transaction [txn (config/db-spec)]
    (some-> (jdbc/query txn ["select * from jobs where id=?" (->uuid id)])
            first
            (update :body json/decode))))
