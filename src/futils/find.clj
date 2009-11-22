(ns futils.find
  #^{:author "James Liu",
     :doc "File utilities"}
  (:use (clojure.contrib.duck-streams )))
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

(defn first-match [m]
  (if (coll? m) (first m) m))

(defn match [regex text]
  (let [m (first-match (re-find (re-pattern regex) text))]
    (if (nil? m)
      [0 0]
      (let [ind (.indexOf text m) len (.length m)]
 [ind (+ ind len)]))))

(defn regexcoach []
  (let [frame (JFrame. "Regular Expression Coach") pane (JPanel.) regexText (JTextField.) 
 targetText (JTextField. "")
 statusBar (JLabel. "Match from 0 to 0")
        keyHandler (proxy [KeyAdapter] [] 
       (keyTyped [keyEvent] 
         (try       
   (let [m (match (.getText regexText) (.getText targetText)) 
         hl (.getHighlighter targetText)
         pen (DefaultHighlighter$DefaultHighlightPainter. Color/RED)]
     (.removeAllHighlights hl)
     (.addHighlight hl (first m) (second m) pen)
     (.setText statusBar (format "Match from %s to %s" (first m) (second m))))
   (catch PatternSyntaxException e (.setText statusBar (.getMessage e))))))]
    (doto regexText
      (.addKeyListener keyHandler))
    (doto targetText
      (.addKeyListener keyHandler))
    (doto pane
      (.setLayout (BoxLayout. pane BoxLayout/Y_AXIS))
      (.add (JLabel. "Regular Expression"))
      (.add regexText)
      (.add (JLabel. "Target String"))
      (.add targetText)
      (.add statusBar))
    (doto frame
      (.add pane)
      (.setSize 300 300)
      (.setVisible true))))

