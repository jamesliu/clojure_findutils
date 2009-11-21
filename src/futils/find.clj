(ns futils.find
  #^{:author "James Liu",
     :doc "File utilities"}
  (:use [ clojure.contrib.duck-streams ]))
(defn find 
  "Return all files"
  [file]
  (file-seq (file-str file)))
