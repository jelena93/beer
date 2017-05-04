(defproject beer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.2"]
                 [selmer "1.10.7"]
                 [ring-server "0.4.0"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [com.cerner/clara-rules "0.14.0"]
                 [buddy/buddy-auth "1.4.1"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [liberator "0.10.0"]
                 [ring/ring-json "0.4.0"]
                 [cheshire "5.2.0"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler beer.handler/app
         :init beer.handler/init
         :destroy beer.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.5.1"]]}})
