(ns ^{:doc "Namespace to manipulate parsed html datastructure to
            extract entities"}
  statuspage-crawler.utils.tag
  (:require [net.cgrand.enlive-html :as html]
            [statuspage-crawler.utils.core :refer [print-vals]]))

(def tag-type->enlive-path
  {:img [:attrs :src]
   :a [:attrs :href]})

(defn extract-tags [tag-type enlive-html]
  (html/select enlive-html [:body tag-type]))

(defn ->tag-src [tag-type tag]
  (if-let [path (get tag-type->enlive-path tag-type)]
    (get-in tag path)
    (throw (Exception. "Could not find enlive path for tag-type : " tag-type))))
