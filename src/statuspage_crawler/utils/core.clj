(ns statuspage-crawler.utils.core
  (:require [clojure.pprint :as pprint]))

(defn print-vals
  "Print the specified args, and return the value of the last arg."
  [& args]
  (apply println
         (cons "*** "
               (map #(if (string? %) % (with-out-str (pprint/pprint %)))
                    args)))
  (last args))
