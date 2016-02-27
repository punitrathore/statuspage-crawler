(ns statuspage-crawler.crawler
  (:require [net.cgrand.enlive-html :as html]
            [statuspage-crawler.tag :as tag])
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

(declare find-all-image-links-in-url)

(defn recursively-crawl-url [url-link img-links level]
  (let [html (fetch-url url-link)
        new-img-links (find-all-tags :img html)
        new-url-links (find-all-tags :a html)]
    (if html
      (->> new-url-links
           (mapcat #(find-all-image-links-in-url %
                                                 (concat img-links new-img-links)
                                                 (inc level))))
      
      img-links)))

(defn find-all-image-links-in-url [url-link img-links level]
  (if (> level 1)
    img-links
    (recursively-crawl-url url-link img-links level)))
