;;;;;;;;;;;;;;;;;;
;; $Rev$
;; textflow is a trivial generator of RFC like call flow (a.k.a sequence diagrams)
;; 
;; Example usage:
;;   (flow [Alice Bob Tzach]
;;          [[mmm Tzach Bob]
;;          [xxx Bob Alice]
;;          []
;;          [zzz Alice Tzach]])
;;          
;;                 Alice                Bob                Tzach
;;                   |                   |         mmm       |
;;                   |                   |<------------------|
;;                   |         xxx       |                   |
;;                   |<------------------|                   |
;;                   |                   |                   |
;;                   |         zzz       |                   |
;;                   |-------------------------------------->|
;;
;; The source are under Eclipse Public License 1.0
;; Comments are welcome!
;;
;;; Tzach  

(ns textflow
  (:use clojure.contrib.seq-utils)
  (:use clojure.contrib.graph))

;; generic util functions

(defn get-pos [elm coll]
  "return the position of elm in coll"
  (first (for [[idx elt] (indexed coll) :when (= elt elm)] idx)))
  
(defn mid [len]
   (inc (int (/ len 2))))

(defn rep-string [astr nstr pos]
  "replace a substiring of astr with nstr starting at position pos"
  (str (subs astr 0 pos) nstr (subs astr (+ pos (count nstr)))))

(defn abs [x]
  (if (> 0 x) (- x) x))

(defn rec-to-strs [f]
  "recursively change parameters to strings"
  (vec (for [c f] 
    (if (coll? c)
        (vec (rec-to-strs c)) 
        (str c)))))

(defmacro rec-to-strs-mac [f]
  (rec-to-strs f))

(defn tail-cons [col e]
  (reverse (cons e (reverse col))))


;;; textflow functions

(def *space-len* 20)

(defn fill-string [times char]
  "create a string from repeating char times times"
   (apply str (for [_ (range times )] char)))

(defn arrow-line [len]
  (fill-string len "-"))

(defn right-arrow [len]
  (str (arrow-line (dec len)) ">"))

(defn left-arrow [len]
  (str "<" (arrow-line (dec len))))
  
(defn pos-in-pic [actor actors]
  (let [pos (get-pos actor actors)]
    (if pos (* *space-len* (inc pos))
            (throw (Exception. (str "actor " actor " not found"))))))
  
(defn write-space 
  ([] (write-space (dec *space-len*)))
  ([len] (fill-string len " ")))
  

(defn write-actors
  "return a string of actors name with appropriate spaces between them" 
  ([actors prev-len]
  (let [actor (first actors)
        len (count actor)]  
    (if actor
      (str
          (write-space (- (pos-in-pic actor actors) (mid len) prev-len))
          actor
          (write-actors (rest actors) (dec (mid len)))))))
  ([actors] (write-actors actors 0)))

(defn write-empty [actors]
  (fill-string (count actors) (str (write-space) "|")))

(defn err-msg [msg clg cld error]
  (str "Problem in message " msg 
          " from " clg 
          " to " cld 
          ": " error))
  
(defn write-msg 
  ([actors msg clg cld]
    (try
      (let [f-pos (pos-in-pic clg actors)
          t-pos (pos-in-pic cld actors)
          start (min f-pos t-pos)
          text (- (+ start (mid *space-len*)) (mid (count msg)))
          len (dec (abs (- f-pos t-pos)))
          arrow (if (< f-pos t-pos) (right-arrow len) (left-arrow len))]
          [(rep-string (write-empty actors) msg text)
            (rep-string (write-empty actors) arrow start)])
      (catch Exception e
        (throw (Exception.
          (err-msg msg clg cld (. e getMessage)))))))
  ([actors]
    [(write-empty actors)]))

(defn write-flow [actors msgs]
  (println (write-actors actors))
  (doseq [msg msgs]
      (doseq [row  (apply write-msg (cons actors msg))]
        (println row))))
        
(defmacro flow [& flow]
  (list 'apply 'write-flow (rec-to-strs flow))) 
  
;; Example    
(flow [Alice Bob Tzach] ; the "actors" are Alice Bob and Tzach
  [[mmm Tzach Bob]
    [xxx Bob Alice]
    [] ; space in the flow
    [zzz Alice Tzach]])