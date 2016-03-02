(ns statuspage-crawler.utils.requests
  (:require [clj-http.client :as http]))

(defn make-new-job-request [port urls]
  (http/post (str "http://localhost:" port "/jobs") {:form-params {:urls urls}
                                                     :content-type :json}))

(defn make-status-job-request [port job-id]
  (http/get (str "http://localhost:" port "/jobs/" job-id "/status")))

(defn make-result-job-request [port job-id]
  (http/get (str "http://localhost:" port "/jobs/" job-id "/result")))

