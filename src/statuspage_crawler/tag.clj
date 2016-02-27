(ns ^{:doc "Namespace to manipulate parsed html datastructure to
            extract entities"}
  statuspage-crawler.tag
  (:require [net.cgrand.enlive-html :as html]))

(def tag-type->enlive-path
  {:img [:attrs :src]
   :a [:attrs :href]})

(defn extract-tags [tag-type enlive-html]
  (html/select enlive-html [:body tag-type]))

(def extract-img-tags #(extract-img-tags :img))
(def extract-link-tags #(extract-img-tags :a))

(defn ->tag-src [tag-type tag]
  (if-let [path (get tag-type->enlive-path tag-type)]
    (get-in tag path)
    (throw (Exception. "Could not find enlive path for tag-type : " tag-type))))
