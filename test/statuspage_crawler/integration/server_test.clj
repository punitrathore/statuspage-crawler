(ns statuspage-crawler.integration.server-test
  (:require [statuspage-crawler.server.server :as server]
            [statuspage-crawler.utils.requests :as requests]
            [clojure.test :refer :all]
            [cheshire.core :as json]))

(def test-port 9011)

(defn integration-fixture [f]
  (server/start-server! test-port)
  (f)
  (server/stop-server!))

(use-fixtures :each integration-fixture)

(deftest test-api-routes
  (testing "happy path"
    (let [new-job-resp (requests/make-new-job-request test-port ["https://statuspage.io"])
          job-id (-> new-job-resp :body json/decode (get "job-id"))]
      (is (= 202 (:status new-job-resp)))
      (is (not (nil? job-id)))
      (Thread/sleep 20000)
      (let [status-resp (requests/make-status-job-request test-port job-id)]
        (is (= 200 (:status status-resp)))
        (is (= {"inprogress" 0,"completed" 64} (-> status-resp :body json/decode))))

      (let [result-resp (requests/make-result-job-request test-port job-id)
            urls-encountered (-> result-resp :body json/decode keys set)]
        (is (= 200 (:status result-resp)))
        (is (> (count urls-encountered) 10)))))

  (testing "when urls passed in are partly garbage, it will drop the bad urls in the response"
    (let [bad-url "foo.bar.xzy"
          new-job-resp (requests/make-new-job-request test-port [bad-url "https://statuspage.io"])
          job-id (-> new-job-resp :body json/decode (get "job-id"))]
      (is (= 202 (:status new-job-resp)))
      (is (not (nil? job-id)))
      (Thread/sleep 5000)
      (let [status-resp (requests/make-status-job-request test-port job-id)]
        (is (= 200 (:status status-resp)))
        (is (= {"inprogress" 0, "completed" 65} (-> status-resp :body json/decode))))

      (let [result-resp (requests/make-result-job-request test-port job-id)
            urls-crawled (-> result-resp :body json/decode keys)]
        (is (= 200 (:status result-resp)))
        (is (> (count urls-crawled) 10))
        ;; foo.bar.xzy should not exist
        (is (false? (contains? (set urls-crawled) bad-url)))))))
