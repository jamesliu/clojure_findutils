(use '[clojure.contrib.fcase :only (case)])

(defmacro nil! [var]
  (list 'def var nil))

(defn nil1! [at]
  (swap! at (fn [_] nil)))

(defmacro nil2! [var]
  `(def ~var nil))

(defmacro nif [p x y z]
  `(case (Integer/signum ~p)
         1 ~x
         0 ~y
         -1 ~z))

(defmacro our-when [p & body]
  `(if ~p
     (do ~@body)))