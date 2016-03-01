# Statuspage.io Crawler Service

Server is running at http://ec2-52-35-249-101.us-west-2.compute.amazonaws.com:9000

## Crawler Design:
* The main namespace is src/domain/crawler.clj. Here we `map` over the urls passed to us, and we recursively crawl any new urls we encounter on them. We keep track of the `level` so that we stop when we exceed the level of crawling. I have used Clojure's `pmap` function to improve speed. It uses a threadpool underneath, so the crawling is automatically parallelized.
* I have also used Clojure's memoize function to speed things up. Given the same parameter(url) it will return the result without any computation.
* I have use the simple library called Dogfish to manage the Postgres DB migrations. It's really simple and easy to use.
* I have used Clojure's Atom to store the job-stats. It is a thread-safe way to maintain state in Clojure. Since it uses Compare and Swap(CAS) underneath, it will work correctly even if multiple threads are trying changing the underlying value.
* If we encounter a `bad` url, I have taken the decision to drop it from the result.

## Future Improvements:
* If a page has been crawled before, we are wasting effort in recrawling the page if it appears again. I have mitigated the computation by memoizing the function. But this comes at a cost. Each memoization will add some memory overhead. We could do better. We can maintain a list of urls already crawled, and everytime we crawl a url, we check for the existence of the url in the list.
* Use a logging library instead of printing.

## License

Copyright © 2016 StatusPage.io

Distributed under the Eclipse Public License either version 1.0
