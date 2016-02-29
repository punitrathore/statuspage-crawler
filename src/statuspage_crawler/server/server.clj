(ns statuspage-crawler.server.server
  (:require [ring.adapter.jetty :as jetty]
            [statuspage-crawler.server.routes :as routes]))

(defonce server (atom nil))

(defn start-server! [port]
  (reset! server (jetty/run-jetty #'routes/handler {:port port :join? false})))

(defn stop-server! []
  (when @server
    (.stop @server)))

(comment
  (start-server! 9000))
