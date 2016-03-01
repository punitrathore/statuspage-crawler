(ns statuspage-crawler.integration.server-test
  (:require [statuspage-crawler.server.server :as server]
            [statuspage-crawler.utils.requests :as requests]
            [clojure.test :refer :all]
            [cheshire.core :as json]))

(def test-port 9000)

(defn integration-fixture [f]
  (server/start-server! test-port)
  (f)
  (server/stop-server!))

(use-fixtures :each integration-fixture)

(deftest test-api-routes
  (testing "happy path"
    (let [new-job-resp (requests/make-new-job-request test-port ["http://statuspage.io"])
          job-id (-> new-job-resp :body json/decode (get "job-id"))]
      (is (= 202 (:status new-job-resp)))
      (is (not (nil? job-id)))
      (Thread/sleep 10000)
      (let [status-resp (requests/make-status-job-request test-port job-id)]
        (is (= 200 (:status status-resp)))
        (is (= {"inprogress" 0 "completed" 2} (-> status-resp :body json/decode))))

      (let [result-resp (requests/make-result-job-request test-port job-id)]
        (is (= 200 (:status result-resp)))
        (def *r result-resp)
        (is (= ["https://www.statuspage.io/"]
               (-> result-resp :body json/decode keys))))))

  (testing "when urls passed in are partly garbage, it will drop the bad urls in the response"
    (let [new-job-resp (requests/make-new-job-request test-port ["foo.bar.xzy" "http://statuspage.io"])
          job-id (-> new-job-resp :body json/decode (get "job-id"))]
      (is (= 202 (:status new-job-resp)))
      (is (not (nil? job-id)))
      (Thread/sleep 3000)
      (let [status-resp (requests/make-status-job-request test-port job-id)]
        (is (= 200 (:status status-resp)))
        (is (= {"inprogress" 0 "completed" 3} (-> status-resp :body json/decode))))

      (let [result-resp (requests/make-result-job-request test-port job-id)]
        (is (= 200 (:status result-resp)))
        (def *r result-resp)
        (is (= ["https://www.statuspage.io/"]
               (-> result-resp :body json/decode keys))))))

  )
