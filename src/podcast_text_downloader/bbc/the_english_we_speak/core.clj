(ns podcast-text-downloader.bbc.the-english-we-speak.core
  (:use [podcast-text-downloader.common
         :only (read-old-content convert-to-docx get-cli-opts)])
  (:use [podcast-text-downloader.bbc.common :only (gen-markdown-file)]))

(def old-md "old_the_english_we_speak.md")
(def old-content (read-old-content old-md))

(defn -main [& args]
  (let [[options args banner] (get-cli-opts args)
        md (gen-markdown-file
            "http://www.bbc.co.uk/learningenglish/english/features/the-english-we-speak"
            "http://downloads.bbc.co.uk/podcasts/worldservice/tae/rss.xml")]
    (when-not (= md old-content)
      (spit old-md md)
      (convert-to-docx old-md (:output-filename options)))))
