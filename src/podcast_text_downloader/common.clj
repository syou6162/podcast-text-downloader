(ns podcast-text-downloader.common
  (:use clj-xpath.core))

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
