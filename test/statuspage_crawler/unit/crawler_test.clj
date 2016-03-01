(ns statuspage-crawler.unit.crawler-test
  (:require [statuspage-crawler.domain.crawler :refer :all]
            [net.cgrand.enlive-html :as html]
            [clojure.test :refer :all]))

(def html "<body>
             <img src=\"/1.png\"/>
             <img src=\"//foo.com/1.png\"/>
             <a href=\"/statuspage.io\">Click me</a>
           </body>")

(deftest test-find-img-tags
  (is (= ["/1.png" "//foo.com/1.png"]
         (find-img-tags (html/html-snippet html)))))

(deftest test-find-link-tags
  (is (= ["/statuspage.io"]
         (find-link-tags (html/html-snippet html)))))
