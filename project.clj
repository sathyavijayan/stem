(defproject stem "1.0.1"
  :description "stem - simple templating engine"
  :url "https://github.com/sathyavijayan/stem"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-midje "3.2.1"]]
                   :dependencies [[midje "1.9.0-alpha9"]]}})
