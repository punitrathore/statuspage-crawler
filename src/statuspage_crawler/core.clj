(ns statuspage-crawler.core
  (:require [clj-http.client :as http]
            [hickory.core :as h]
            [hickory.select :as s]
            [net.cgrand.enlive-html :as html])
  (:import [java.net URL]))

(defn fetch-url [url]
  (html/html-resource (URL. url)))

(defn crawl-url [url]
  (let [resp (http/get url)
        html (:body resp)
        html-map (h/parse html)]
    html-map))

(defn extract-tags [tag-type enlive-html]
  (html/select enlive-html [:body tag-type]))

(def extract-img-tags #(extract-img-tags :img))
(def extract-link-tags #(extract-img-tags :a))

(def tag-type->enlive-path
  {:img [:attrs :src]
   :a [:attrs :href]})

(defn ->tag-src [tag-type tag]
  (if-let [path (get tag-type->enlive-path tag-type)]
    (get-in tag path)
    (throw (Exception. "Could not find enlive path for tag-type : " tag-type) )))


;; http://google.com/images/branding/googlelogo/1x/googlelogo_white_background_color_272x92dp.png
;; https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png
;;                      /images/branding/googleg/1x/googleg_standard_color_128dp.png
