(ns ^{:doc "Routes + Handler functions"}
  statuspage-crawler.server.routes
  (:require [ring.middleware.json :as ring-json]
            [cheshire.core :as json]
            [compojure.core :refer [defroutes context routes GET POST PUT]]
            [compojure.route :as route]
            [statuspage-crawler.domain.crawler :as crawler]
            [statuspage-crawler.domain.job-stats :as job-stats]
            [statuspage-crawler.server.db :as db]
            [statuspage-crawler.utils.core :refer :all]))

(defn not-found-response [job-id]
  {:status 404
   :body (str "Could not find job with id : " job-id)})

(defn ->ring-response [status body]
  {:status status
   :body body
   :content-type "application/json"})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;; handlers

(defn create-new-job [{:keys [params]}]
  (let [urls (get params "urls")
        job-row (db/create-new-job)
        job-id (:id job-row)]
    (future
      (crawler/images-in-urls job-id urls))
    (->ring-response 202 (json/encode {:job-id job-id}))))

(defn status [{:keys [params]} job-id]
  (if-let [job (db/find-job job-id)]
    (let [job-stats (or (job-stats/stats job-id)
                        (json/decode (:stats job)))]
      (->ring-response 200 (json/encode job-stats)))
    (not-found-response job-id)))

(defn result [{:keys [params]} job-id]
  (if-let [job (db/find-job job-id)]
    (if (= "completed" (:status job))
      (->ring-response 200 (json/encode (:body job)))
      (->ring-response 200 "Job is still running. Check back later."))
    (not-found-response job-id)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def all-routes
  (routes
   (GET "/foo" []
        (fn [req]
          {:body "bar"}))
   
   (POST "/jobs" [] create-new-job)
   
   (GET "/jobs/:job-id/status" [job-id]
        #(status % job-id ))

   (GET "/jobs/:job-id/result" [job-id]
        #(result % job-id ))

   (route/not-found "<h1>Page Not Found</h1>")))

(defn trace-info [request response]
  {:request request
   :response response})

(defn wrap-tracing [handler]
  (fn [request]
    (try
      (let [response (handler request)]
        ;; ideally we would use a logging library here
        (future (println (trace-info request response)))
        response)
      (catch Exception e
        (.printStackTrace e)
        {:status 500
         :body (.getStactTrace e)}))))

(defn handler [req]
  (let [handler-fn (-> all-routes
                       wrap-tracing
                       ring-json/wrap-json-params)]
    (handler-fn req)))
