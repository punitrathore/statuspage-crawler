(ns ^{:doc "Namespace for manipulating href & img src links"}
  statuspage-crawler.utils.link)

(def img-regex #"(.*\.(?:png|jpg|gif))")
;;(boolean (re-find img-regex link))

(defn img-link? [^String link]
  (and link
       (or (.contains link ".png")
           (.contains link ".gif")
           (.contains link ".jpg"))))

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

(defn- trim-trailing-backslash [^String s]
  (if (and s (.endsWith s "/"))
    (subs s 0 (dec (count s)))
    s))

(defn fully-qualify-url [^String url ^String new-url]
  (let [new-url (trim-trailing-backslash new-url)]
   (cond (not new-url)
         nil

         (or (.contains new-url "http://")
             (.contains new-url "https://"))
         new-url

         (or (.startsWith new-url "//"))
         (let [http-protocol (if (.contains url "http://")
                               "http:"
                               "https:")]
           (str http-protocol new-url))
        
         :else
         (str url new-url))))
