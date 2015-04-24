(ns podcast-text-downloader.six-minute-english.core
  (:use clj-xpath.core)
  (:use [podcast-text-downloader.common
         :only (read-old-content get-items-from-rss convert-to-docx get-cli-opts)])
  (:use [podcast-text-downloader.six-minute-english.content-fetch
         :only (content-fetch-from-6-minute-english)])
  (:use [clj-utils.string :only (levenshtein-distance)]))

(def old-md "old_six_minute_english.md")
(def old-content (read-old-content old-md))

(def title-to-url-map-of-6-minute-english
  (let [url "http://www.bbc.co.uk/learningenglish/persian/features/6-minute-english"
        html (->> (clojure.string/split (slurp url) #"\n")
                  (map clojure.string/trim)
                  (clojure.string/join #""))
        regex #"(?s)<div class=\"text\"><h2><a  href=\"(.*?)\">(.*?)</a></h2>"]
    (->> html
         (re-seq regex)
         (map
          (fn [[_ url title]]
            [title (str "http://www.bbc.co.uk" url)]))
         (into {}))))

(defn get-most-possible-url [title']
  (->> title-to-url-map-of-6-minute-english
       (map
        (fn [[title url]]
          [(levenshtein-distance title' title) title url]))
       (sort-by first)
       (first)
       last))

(defn gen-markdown-file []
  (let [items (-> (slurp "http://downloads.bbc.co.uk/podcasts/worldservice/how2/rss.xml")
                  (xml->doc)
                  (get-items-from-rss
                   (fn [item]
                     (-> (:title item)
                         (get-most-possible-url)
                         slurp
                         content-fetch-from-6-minute-english))))]
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
