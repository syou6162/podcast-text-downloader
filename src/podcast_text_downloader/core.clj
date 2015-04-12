(ns podcast-text-downloader.core
  (:use clj-xpath.core)
  (:use [podcast-text-downloader.common :only (get-items-from-rss)])
  (:use [podcast-text-downloader.content-fetch.core
         :only (content-fetch-from-scientificamerican)]))

(defn -main [& args]
  (let [items (-> (slurp "http://rss.sciam.com/sciam/60secsciencepodcast")
                  (xml->doc)
                  (get-items-from-rss
                   (fn [item]
                     (-> item
                         :link
                         slurp
                         content-fetch-from-scientificamerican))))]
    (->> items
         (map
          (fn [item]
            (str
             "# " (:title item) "\n"
             "- " (:site-title item) "\n"
             "- " (:pubDate item) "\n"
             (:content item))))
         (clojure.string/join "\n")
         (println))))
