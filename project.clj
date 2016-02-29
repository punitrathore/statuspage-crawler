(defproject statuspage-crawler "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "2.1.0"]
                 [hickory "0.6.0"]
                 [enlive "1.1.6"]
                 [me.raynes/fs "1.4.6"]
                 [clojurewerkz/urly "1.0.0"]
                 [ring "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.4.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [cheshire "5.5.0"]])
