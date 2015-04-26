(defproject podcast-text-downloader "0.0.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.github.kyleburton/clj-xpath "1.4.3"]
                 [org.clojure/tools.cli "0.2.1"]
                 [info.yasuhisay/clj-utils "0.1.3"]
                 [org.jsoup/jsoup "1.7.2"]]
  :jvm-opts ["-Xms2G" "-Xmx2G" "-server"]
  :main podcast-text-downloader.core)
