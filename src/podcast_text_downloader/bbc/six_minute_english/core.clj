(ns podcast-text-downloader.bbc.six-minute-english.core
  (:use [podcast-text-downloader.common
         :only (read-old-content convert-to-docx get-cli-opts)])
  (:use [podcast-text-downloader.bbc.common :only (gen-markdown-file)]))

(def old-md "old_six_minute_english.md")
(def old-content (read-old-content old-md))

(defn -main [& args]
  (let [[options args banner] (get-cli-opts args)
        md (gen-markdown-file
            "http://www.bbc.co.uk/learningenglish/persian/features/6-minute-english"
            "http://downloads.bbc.co.uk/podcasts/worldservice/how2/rss.xml")]
    (when-not (= md old-content)
      (spit old-md md)
      (convert-to-docx old-md (:output-filename options)))))
