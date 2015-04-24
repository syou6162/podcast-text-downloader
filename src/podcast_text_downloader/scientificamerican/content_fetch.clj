(ns podcast-text-downloader.scientificamerican.content-fetch)

(defn content-fetch-from-scientificamerican [html]
  (let [regex #"(?s)<section class=\"article-content\">(.*?)</section>"
        content (->> html
                     (re-find regex)
                     (second))]
    content))
