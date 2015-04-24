(ns podcast-text-downloader.bbc.common
    (:use clj-xpath.core)
  (:use [podcast-text-downloader.common
         :only (read-old-content get-items-from-rss convert-to-docx get-cli-opts)])
  (:use [clj-utils.string :only (levenshtein-distance)]))

(import org.jsoup.Jsoup)

(defn content-fetch [html]
  (let [regex #"(?s)<div class=\"text\" dir=\"ltr\">(.*?)</div>"
        content (->> html
                     (re-find regex)
                     (second))]
    (-> content
        (clojure.string/replace #"</p>" "@@@")
        (clojure.string/replace #"<br>" "@@@")
        (Jsoup/parse)
        (.text)
        (clojure.string/replace #"@@@" "\n\n"))))

(defn get-most-possible-url-fn [base-url]
  (fn [title']
    (let [html (->> (clojure.string/split (slurp base-url) #"\n")
                    (map clojure.string/trim)
                    (clojure.string/join #""))
          regex #"(?s)<div class=\"text\"><h2><a  href=\"(.*?)\">(.*?)</a></h2>"
          title-to-url-map (->> html
                                (re-seq regex)
                                (map
                                 (fn [[_ url title]]
                                   [title (str "http://www.bbc.co.uk" url)]))
                                (into {}))]
      (->> title-to-url-map
           (map
            (fn [[title url]]
              [(levenshtein-distance title' title) title url]))
           (sort-by first)
           (first)
           last))))

(defn gen-markdown-file [base-url rss-url]
  (let [get-most-possible-url (get-most-possible-url-fn base-url)
        items (-> (slurp rss-url)
                  (xml->doc)
                  (get-items-from-rss
                   (fn [item]
                     (println item)
                     (-> (:title item)
                         (get-most-possible-url)
                         slurp
                         content-fetch))))]
    (->> items
         (map
          (fn [item]
            (str
             "# " (:title item) "\n"
             "- " (:site-title item) "\n"
             "- " (:pubDate item) "\n\n"
             (:content item))))
         (clojure.string/join "\n"))))
