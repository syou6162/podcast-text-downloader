(ns podcast-text-downloader.common
  (:use clj-xpath.core)
  (:use [clojure.java.shell :only [sh]])
  (:require [clojure.tools.cli :as cli]))

(defn get-items-from-rss [rss-doc content-fetch-fn]
  (let [title ($x:text "/rss/channel/title" rss-doc)]
    (->> ($x "/rss/channel/item" rss-doc)
         (map
          (fn [item]
            (let [link ($x:text "./link" item)
                  item {:site-title title
                        :title ($x:text "./title" item)
                        :link link
                        :pubDate ($x:text "./pubDate" item)}
                  content (-> (content-fetch-fn item)
                              (clojure.string/replace #"<br />" "\n"))]
              (-> item
                  (assoc :content content)))))
         (vec))))

(defn convert-to-docx [old-md output-filename]
  (sh "pandoc" "-f" "markdown+ignore_line_breaks" old-md "-o" output-filename))

(defn get-cli-opts [args]
  (cli/cli args ["--output-filename"]))

(defn read-old-content [filename]
  (when-not (.exists (java.io.File. filename))
    (sh "touch" filename))
  (slurp filename))
