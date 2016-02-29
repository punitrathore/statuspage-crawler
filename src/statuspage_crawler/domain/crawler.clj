(ns statuspage-crawler.domain.crawler
  (:require [net.cgrand.enlive-html :as html]
            [cheshire.core :as json]
            [statuspage-crawler.config :as config]
            [statuspage-crawler.server.db :as db]
            [statuspage-crawler.tag :as tag]
            [statuspage-crawler.util :as util]
            [statuspage-crawler.link :as link]
            [clojurewerkz.urly.core :as urly])
  (:import [java.net URL]))

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

(declare find-all-image-links-in-url*)

(defn- find-imgs-and-recursively-crawl-new-urls [url-link img-links level]
  (let [html (fetch-url url-link)
        new-img-links (find-img-tags html)
        new-url-links (->> (find-link-tags html)
                           (mapv #(link/fully-qualify-url url-link %)))]
    (if (empty? new-url-links)
      img-links
      (->> new-url-links
           (mapcat #(find-all-image-links-in-url* %
                                                  (conj img-links
                                                        [url-link new-img-links])
                                                  (inc level)))
           (into [])))))

(defn- find-all-image-links-in-url* [url-link img-links level]
  (if (= level (config/max-recursive-level))
    img-links
    (find-imgs-and-recursively-crawl-new-urls url-link img-links level)))

(defn find-all-image-links-in-url [url]
  (let [domain->imgs (find-all-image-links-in-url* url #{} 0)]
    (->> domain->imgs
         link/filter-and-fully-qualify-valid-images
         (filter (fn [[url imgs]]
                   (not (empty? imgs))))
         (into {}))))

(defn images-in-urls [job-id urls]
  (let [url->images (reduce (fn [url->imgs url]
                              (merge url->imgs
                                     (find-all-image-links-in-url url)))
                            {} urls)]
    (db/update-job job-id {:body (json/encode url->images)
                           :status "completed"})))
