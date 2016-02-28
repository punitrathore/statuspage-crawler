(ns statuspage-crawler.unit.link-test
  (:require [statuspage-crawler.link :refer :all]
            [clojure.test :refer :all]))

(deftest test-img-link
  (is (true? (img-link? "/images/foo/img1.png?abc=232382")))
  (is (true? (img-link? "http://statuspage.io/images/foo/img2.jpg?abc=232382")))
  (is (true? (img-link? "//cdn.foo.com/images/foo/img3.gif?abc=232382")))
  (is (false? (img-link? "//cdn.foo.com/images/foo/img3.pdf?abc=232382"))))

(deftest test-fully-qualify-img
  (is (= "http://cdn.com/1.png"
         (fully-qualify-img "abc.com" "//cdn.com/1.png")))
  (is (= "abc.com/1.png"
         (fully-qualify-img "abc.com" "/1.png"))))

(deftest test-fully-qualify-url
  (is (= "a.com/b.html"
         (fully-qualify-url "a.com" "/b.html")))

  (is (= "b.com/html"
         (fully-qualify-url "a.com" "b.com/c.html"))))


(deftest test-filter-and-fully-qualify-valid-images
  (is (= [["a.com" #{"a.com/1.png"}]
          ["http://b.com" #{"http://c.com/3.png"}]]
         (filter-and-fully-qualify-valid-images [["a.com" ["/1.png" "2.pdf"]]
                                                 ["http://b.com" ["//c.com/3.png"]]]))))
