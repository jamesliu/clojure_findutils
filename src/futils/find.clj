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

(defn find-classes [regex]
  (let [search-path-properities ["java.class.path" "sun.boot.class.path"]]
    (for [search-path search-path-properities
          jar (filter #(.endsWith % ".jar")
                      (.split (System/getProperty search-path)
                              (System/getProperty "path.separator")))
          entry (try (filter #(.endsWith (.getName %) ".class")
                             (enumeration-seq (.entries (new java.util.jar.JarFile jar))))
                     (catch Exception _))
          name [(.. entry getName (replaceAll "\\.class$" ""))]
          :when (re-find regex name)]
      name)))


