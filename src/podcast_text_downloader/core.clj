(ns podcast-text-downloader.core
  (:use clj-xpath.core)
  (:use [clojure.java.shell :only [sh]])
  (:use [podcast-text-downloader.common :only (get-items-from-rss)])
  (:use [podcast-text-downloader.content-fetch.core
         :only (content-fetch-from-scientificamerican)]))

(def old-md "old.md")
(def old-content (slurp old-md))

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

(defn convert-to-docx []
  (sh "pandoc" "-f" "markdown+ignore_line_breaks" old-md "-o" "test.docx"))

(defn -main [& args]
  (let [md (gen-markdown-file)]
    (when-not (= md old-content)
      (spit old-md md)
      (convert-to-docx))))
