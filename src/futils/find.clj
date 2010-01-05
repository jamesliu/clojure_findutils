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

(defn filter-regex
  [regex files]
  (if (= regex nil)
    files
    (filter #(re-find re %) files)))

(defn addfilter
  [fn1 fn2 & args]
  (comp (apply partial fn1 args) fn2))

(defn -main [& args]
  (with-command-line args
    "Unix find command"
    [[name "file name" ".*"]
     [type "file type"]
     [regex "regular expression"]
     [help? h? "help"]
     remaining "."]
    ;(println "dir: " (first remaining))
    ;(println "name: " name)
    (def ufind find-files)
    (def ufind (addfilter filter-filename ufind (re-pattern name)))
    (def ufind (addfilter filter-filename ufind (re-pattern regex)))
    (def ufind (addfilter filter-filetype ufind type))
    (println (ufind (first remaining)))))


