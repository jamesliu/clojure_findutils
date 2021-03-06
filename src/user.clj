(println "Running user startup")
(use 'clojure.contrib.repl-utils)
(use 'clojure.contrib.str-utils)
(use 'clojure.contrib.java-utils)
(use 'clojure.contrib.def)
(use 'clojure.contrib.ns-utils)
(use 'clojure.contrib.seq-utils)
(use 'clojure.contrib.test-is)
(use 'clojure.contrib.sql)
(use 'clojure.stacktrace)
(use 'clojure.contrib.duck-streams)
(use 'futils.find)

(set! *warn-on-reflection* true)

;;; The NS that we are testing/working on.
;;; This can be given through the java property work-ns, e.g.
;;;   ./clj -Dwork-ns=xxx.yyy
(def *work-ns*
  (symbol
    (or (System/getProperty "work-ns")
        'futils.find)))

;;; A testing NS.
(def *test-ns*
  (symbol
    (or (System/getProperty "test-ns")
        (symbol
          (re-sub #"[^\.]+$" #(str "test-" %)
                  (name *work-ns*))))))

; (use *work-ns*)
(defn l [] (binding [*warn-on-reflection* true]
             (use *work-ns* :reload)))
(defn t []
  (binding [*warn-on-reflection* true]
    (require *test-ns* :reload)
    (run-tests *test-ns*)))
(defn i [] (in-ns *work-ns*))

; Load the User's test-user.clj if present.
(try (load "/test-user")
  (catch java.io.FileNotFoundException e
    (println "No test-user.clj found in classpath.")))

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
 
