(ns podcast-text-downloader.core
  (:use clj-xpath.core)
  (:use [clojure.java.shell :only [sh]])
  (:require [clojure.tools.cli :as cli])
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

(defn convert-to-docx [output-filename]
  (sh "pandoc" "-f" "markdown+ignore_line_breaks" old-md "-o" output-filename))

(defn- get-cli-opts [args]
  (cli/cli args
           ["--output-filename"]))

(defn -main [& args]
  (let [[options args banner] (get-cli-opts args)
        md (gen-markdown-file)]
    (when-not (= md old-content)
      (spit old-md md)
      (convert-to-docx (:output-filename options)))))
