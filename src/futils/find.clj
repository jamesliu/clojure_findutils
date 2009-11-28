(ns 
  #^{:author "James Liu"
     :doc "File utilities"}
  futils.find
  (:gen-class)
  (:use clojure.contrib.duck-streams 
        clojure.contrib.str-utils    
        clojure.contrib.java-utils 
        clojure.contrib.command-line)
  (:import (java.io File)))

(defn find-files
  "Return all directories and files"
  [file]
  (file-seq (file-str file)))

(defn filter-filename
  "Return the filtered filename"
  [re files]
  (filter #(re-find re (.getName %)) files))

(defn filter-filetype
  [ftype files]
  (cond 
    (= ftype nil) files
    (= ftype "d") (filter #(.isDirectory %) files)
    (= ftype "f") (filter #(.isFile %) files)
    :else files))

(defn addfilter
  [fn & args]
  (comp (apply partial fn args) ufind))

(def ufind find-files)

(defn -main [& args]
  (with-command-line args
    "Unix find command"
    [[name "file name" ".*"]
     [type "file type"]
     [help? h? "help"]
     remaining]
    ;(println "dir: " (first remaining))
    ;(println "name: " name)
    (def ufind (addfilter filter-filename (re-pattern name)))
    (def ufind (addfilter filter-filetype type))
    (println (ufind (first remaining)))))


