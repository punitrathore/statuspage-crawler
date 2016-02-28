(ns ^{:doc "Namespace to manipulate parsed html datastructure to
            extract entities"}
  statuspage-crawler.tag
  (:require [net.cgrand.enlive-html :as html]))

(def tag-type->enlive-path
  {:img [:attrs :src]
   :a [:attrs :href]})

(defn extract-tags [tag-type enlive-html]
  (html/select enlive-html [:body tag-type]))

(defn ->tag-src [tag-type tag]
  (if-let [path (get tag-type->enlive-path tag-type)]
    (let [src (get-in tag path)]
      (when (nil? src)
        (println "->tag-str returned NIL!   tag-type:" tag-type "     tag:" tag))
      src)
    (throw (Exception. "Could not find enlive path for tag-type : " tag-type))))
