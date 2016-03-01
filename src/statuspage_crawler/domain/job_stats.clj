(ns ^{:doc "Functions to add statistics for a job"}
  statuspage-crawler.domain.job-stats)

(defonce job-stats (atom {}))

(defn new-stat [job-id]
  (swap! job-stats assoc job-id {:inprogress 0 :completed 0}))

(defn inc-stat [stat job-id]
  (swap! job-stats update-in [job-id stat] inc))

(defn dec-stat [stat job-id]
  (swap! job-stats update-in [job-id stat] dec))

(defn stats [job-id]
  (get @job-stats job-id))

(def inc-inprogress (partial inc-stat :inprogress))
(def dec-inprogress (partial dec-stat :inprogress))

(def inc-completed (partial inc-stat :completed))
(def dec-completed (partial dec-stat :completed))
