(ns ^{:doc "Functions to start and stop the api server"}
  statuspage-crawler.server.server
  (:require [ring.adapter.jetty :as jetty]
            [statuspage-crawler.server.routes :as routes]
            [clojure.tools.nrepl.server :as nrepl]))

(defonce server (atom nil))
(defonce nrepl-server (atom nil))

(defn start-server! [port]
  (reset! server (jetty/run-jetty #'routes/handler {:port port :join? false})))

(defn start-nrepl-server! [port]
  (nrepl/start-server :port (+ 10 port)))

(defn stop-server! []
  (when @server
    (.stop @server)))

(defn start-services! [port]
  (start-server! port)
  (start-nrepl-server! (+ port 10)))

(comment
  (start-server! 9000))
