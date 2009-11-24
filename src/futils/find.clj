(ns 
  #^{:author "James Liu"
     :doc "File utilities"}
  futils.find 
  (:use clojure.contrib.duck-streams clojure.contrib.str-utils clojure.contrib.java-utils)
  (:import (java.io File)))

(defn find-files
  "Return all directories and files"
  [file]
  (file-seq (file-str file)))

