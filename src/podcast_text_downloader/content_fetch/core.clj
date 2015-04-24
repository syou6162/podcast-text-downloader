(ns podcast-text-downloader.content-fetch.core)

(defn content-fetch-from-scientificamerican [html]
  (let [regex #"(?s)<section class=\"article-content\">(.*?)</section>"
        content (->> html
                     (re-find regex)
                     (second))]
    content))

(defn content-fetch-from-6-minutes-english [html]
  (let [regex #"(?s)<div class=\"text\" dir=\"ltr\">(.*?)</div>"
        content (->> html
                     (re-find regex)
                     (second))]
    content))
