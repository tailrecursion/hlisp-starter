(ns flapjax.dom
  (:require
    [hlisp.env  :as hl]
    [jayq.core  :as jq]
    [jayq.util  :as ju]))

(defn id      [e] (peek (ids e)))
(defn ids     [e] (.-ids e))
(defn id!     [e] (if-not (seq (ids e)) (hl/clone e) e))
(defn is-jq?  [e] (string? (.-jquery e)))

(defn filter-id
  [x]
  (fn [v]
    (< 0 (->
           (jq/$ (.-target v))
           (.parentsUntil "body")
           (.andSelf)
           (.filter (str "[data-hl~='" x "']"))
           (.size)))))

(defn filter-not-disabled
  [v]
  (->
    (jq/$ (.-target v))
    (.is "[disabled]")
    not))

(defn find-id
  [x]
  (jq/$ (str "[data-hl~='" x "']")))

(def dom-get (comp find-id id))

(defn value!
  [elem]
  (.val (dom-get elem)))

(defn attr!
  ([elem k]
   (jq/attr (dom-get elem) k))
  ([elem k v & kvs]
   (let [e (dom-get elem)] 
     (mapv (fn [[k v]]
             (case v
               true   (jq/attr e k k)
               false  (.removeAttr e k)
               (jq/attr e k v)))
           (partition 2 (list* k v kvs)))
     elem)))

(defn remove-attr!
  [elem k & ks]
  (let [e (.removeAttr (dom-get elem) k)]
    (when (seq ks)
      (mapv #(.removeAttr e %) ks))
    elem))

(defn toggle-class!
  ([elem c] 
   (.toggleClass (dom-get elem) c)) 
  ([elem c switch] 
   (.toggleClass (dom-get elem) c switch)))

(defn add-class!
  [elem c & cs]
  (let [e (.addClass (dom-get elem) c)]
    (when (seq cs)
      (mapv #(.addClass e %) cs))
    elem))

(defn remove-class!
  [elem c & cs]
  (let [e (.removeClass (dom-get elem) c)]
    (when (seq cs)
      (mapv #(.removeClass e %) cs))
    elem))

(defn css!
  ([elem k v]
   (.css (dom-get elem) k v)
   elem)
  ([elem o]
   (let [ret (.css (dom-get elem) (ju/clj->js o))]
     (if (is-jq? ret) elem ret))))

(defn toggle!
  [elem v]
  (.toggle (dom-get elem) v))

(defn disabled?
  [elem]
  (.is (dom-get elem) "[disabled]"))
