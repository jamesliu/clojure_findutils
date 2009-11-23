(use 'clojure.contrib.duck-streams)
(def completions
  (reduce concat (map (fn [p] (keys (ns-publics (find-ns p))))
    '(clojure.core clojure.set clojure.xml clojure.zip clojure.contrib.duck-streams))))

(with-open [f (java.io.BufferedWriter. (java.io.FileWriter. (str (or (System/getenv "HOME") (System/getenv "HOMEPATH")) "/.clj_completions")))]
    (.write f (apply str (interleave completions (repeat "\n")))))


