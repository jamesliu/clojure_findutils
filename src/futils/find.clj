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
  ([] (find-files ".") )
  ([file]
      (file-seq (file-str file))))

(defn filter-filename
  "Return the filtered filename"
  [name files]
  ; r3 is evaluated first, order does matter
  (let [r1 (partial re-sub #"^[.][*]" "^[^.].*") ; '.a.sh' is NOT included in *.sh
        r2 (partial re-gsub #"[*]" ".*")         ; replace '*' with '.*'
        r3 (partial re-gsub #"[.]" "[.]")        ; '.' is '.' itself
        rename (re-pattern ((comp r1 r2 r3) name))]
    (filter #(re-matches rename (.getName %)) files)))

(defn filter-filetype
  [ftype files]
  (cond 
    (= ftype nil) files
    (= ftype "d") (filter #(.isDirectory %) files)
    (= ftype "f") (filter #(.isFile %) files)
    :else files))

(defn filter-regex
  [regex files]  
  (filter #(re-matches regex (.getPath %)) files))

(defn addfilter
  [fn1 fn2 & args]
  (comp (apply partial fn1 args) fn2))

(defn pretty-print
  [files]
  (doseq [f files] (println (.getPath f))))

(defn -main [& args]
  (with-command-line args
    "Unix find command"
    [[name "file name"]
     [type "file type"]
     [regex "regular expression"]
     [help? h? "help"]
     remaining]
    (def ufind find-files)
    (if name
      (def ufind (addfilter filter-filename ufind name)))
    (if type
      (def ufind (addfilter filter-filetype ufind type))) 
    (if regex
      (def ufind (addfilter filter-regex ufind (re-pattern regex)))) 
    (if (empty? remaining)
      (pretty-print (ufind "."))
      (pretty-print (ufind (first remaining))))))


