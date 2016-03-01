(ns statuspage-crawler.main
  (:require [statuspage-crawler.server.server :as server]))

(defn -main []
  (println "Starting server at port 9000")
  (server/start-server! 9000)
  (println "Started server!"))
