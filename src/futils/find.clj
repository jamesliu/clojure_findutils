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

(defn -main [& args]
  (with-command-line args
    "Unix find command"
    [[foo "This is the description for foo" 1]
     [bar "This is the description for bar" 2]
     [boolean? b? "This is a boolean flag."]
     remaining]
    (println "foo: " foo)
    (println "bar: " bar)
    (println "boolean?: " boolean?)
    (println "remaining: " remaining)))


