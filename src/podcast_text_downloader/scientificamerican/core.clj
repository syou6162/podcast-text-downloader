(ns podcast-text-downloader.scientificamerican.core
  (:use clj-xpath.core)
  (:use [clojure.java.shell :only [sh]])
  (:require [clojure.tools.cli :as cli])
  (:use [podcast-text-downloader.common
         :only (read-old-content get-items-from-rss convert-to-docx get-cli-opts)])
  (:use [podcast-text-downloader.scientificamerican.content-fetch
         :only (content-fetch-from-scientificamerican)]))

(def old-md "old_scientificamerican.md")
(def old-content (read-old-content old-md))

(defn gen-markdown-file []
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
         (clojure.string/join "\n"))))

(defn -main [& args]
  (let [[options args banner] (get-cli-opts args)
        md (gen-markdown-file)]
    (when-not (= md old-content)
      (spit old-md md)
      (convert-to-docx old-md (:output-filename options)))))
