(ns ^{:doc "All logic to crawl urls"}
  statuspage-crawler.domain.crawler
  (:require [net.cgrand.enlive-html :as html]
            [cheshire.core :as json]
            [statuspage-crawler.config :as config]
            [statuspage-crawler.server.db :as db]
            [statuspage-crawler.utils.tag :as tag]
            [statuspage-crawler.domain.job-stats :as job-stats]
            [statuspage-crawler.utils.core :refer :all]
            [statuspage-crawler.utils.link :as link]
            [clojure.core.memoize :as memo])
  (:import [java.net URL]))

(declare find-all-image-links-in-url)

(defn fetch-url [url]
  (try
    (html/html-resource (URL. url))
    (catch Exception e
      (println "Exception while trying to crawl url : " url)
      nil)))

(defn find-all-tags [tag-type enlive-html]
  (some->> enlive-html
           (tag/extract-tags tag-type)
           (map #(tag/->tag-src tag-type %))))

(def find-img-tags (partial find-all-tags :img))
(def find-link-tags (partial find-all-tags :a))

(defn img-links+new-url-links [url-link]
  (let [html (fetch-url url-link)
        new-img-links (find-img-tags html)
        new-url-links (->> (find-link-tags html)
                           (mapv #(link/fully-qualify-url url-link %)))]
    [new-img-links new-url-links]))

;; Note by Punit Feb 29 2016 - this function is memoized for speedup
(def memoized-img-links+new-url-links
  (memoize img-links+new-url-links))

(defn recursively-crawl-url [job-id level url->imgs url]
  (if (= level (config/max-recursive-level))
    url->imgs
    (let [_ (job-stats/inc-inprogress job-id)
          [imgs new-urls] (memoized-img-links+new-url-links url)
          new-url->imgs (assoc url->imgs url imgs)]
      (job-stats/dec-inprogress job-id)
      (job-stats/inc-completed job-id)
      (->> new-urls
           (map #(recursively-crawl-url job-id (inc level) new-url->imgs %))
           (apply merge)))))

(defn crawl-and-fully-qualify-images [job-id url]
  (let [url->imgs (recursively-crawl-url job-id 0 {} url)]
    url->imgs
    (->> url->imgs
         link/filter-and-fully-qualify-valid-images
         (filter (fn [[url imgs]]
                   (not (empty? imgs))))
         (into {}))))

(defn images-in-urls [job-id urls]
  (job-stats/new-stat job-id)
  (let [url->images (->> urls
                         (pmap (fn [url]
                                 (crawl-and-fully-qualify-images job-id url)))
                         (apply merge))]
    (println "Finished crawling urls, going to save to the DB for job-id : " job-id)
    (db/update-job job-id {:body (json/encode url->images)
                           :status "completed"
                           :stats (json/encode (job-stats/stats job-id))})
    (println "Finished job-id : " job-id)))
