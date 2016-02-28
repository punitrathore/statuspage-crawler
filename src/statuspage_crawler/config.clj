(ns statuspage-crawler.config)

(def config
  {:max-recursive-level 2})

(defn max-recursive-level []
  (or (get-in config [:max-recursive-level]) 2))
