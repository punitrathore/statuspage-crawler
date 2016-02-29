(ns statuspage-crawler.config)

(def config
  {:max-recursive-level 2
   :db-spec {:dbtype "postgresql"
             :dbname "statuspage_dev"
             :user "statuspage"
             :password "password"
             :port 5432
             :host "localhost"}})

(defn max-recursive-level []
  (or (:max-recursive-level config) 2))

(defn db-spec []
  (:db-spec config))
