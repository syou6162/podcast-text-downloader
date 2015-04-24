(ns podcast-text-downloader.six-minute-english.content-fetch)

(defn content-fetch-from-6-minute-english [html]
  (let [regex #"(?s)<div class=\"text\" dir=\"ltr\">(.*?)</div>"
        content (->> html
                     (re-find regex)
                     (second))]
    content))
