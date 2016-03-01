(ns ^{:doc "Namespace for manipulating href & img src links"}
  statuspage-crawler.domain.link
  (:require [clojurewerkz.urly.core :as urly]))

(def img-regex #"(.*\.(?:png|jpg|gif))")
;;(boolean (re-find img-regex link))

(defn img-link? [^String link]
  (or (.contains link ".png")
      (.contains link ".gif")
      (.contains link ".jpg")))

(defn fully-qualify-img [^String url ^String img-url]
  (cond (.startsWith img-url "//")
        (str "http:" img-url)

        (.startsWith img-url "/")
        (str url img-url)

        :else
        img-url))

(defn filter-and-fully-qualify-valid-images [url->images]
  (mapv (fn [[url images]]
          [url (->> images
                    (filter img-link?)
                    (map #(fully-qualify-img url %))
                    (into #{}))])
        url->images))

(defn fully-qualify-url [url new-url]
  (cond (not new-url)
        nil

        (or (.contains new-url "http://")
            (.contains new-url "https://"))
        new-url
        
        :else
        (str url new-url)))
