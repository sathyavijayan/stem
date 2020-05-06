(defproject sats/stem "0.1.0-alpha1"
  :description "stem - simple templating library"
  :url "https://github.com/sathyavijayan/stem"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [borkdude/sci "0.0.13-alpha.17"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :test {:plugins [[lein-midje "3.2.1"]]
                   :dependencies [[midje "1.9.9"]]}

             :dev {:plugins [[lein-midje "3.2.1"]]
                   :dependencies [[midje "1.9.9"]]}})
