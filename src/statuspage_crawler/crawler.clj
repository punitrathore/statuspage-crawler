(ns statuspage-crawler.crawler
  (:require [net.cgrand.enlive-html :as html]
            [statuspage-crawler.config :as config]
            [statuspage-crawler.tag :as tag]
            [statuspage-crawler.util :as util]
            [statuspage-crawler.link :as link]
            [kits.map :as m])
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

(declare find-all-image-links-in-url*)

(defn- find-imgs-and-recursively-crawl-new-urls [url-link img-links level]
  (let [html (fetch-url url-link)
        new-img-links (find-all-tags :img html)
        new-url-links (->> (find-all-tags :a html)
                           (mapv #(util/fully-qualify-url url-link %)))]
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
