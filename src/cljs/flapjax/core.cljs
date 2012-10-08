(ns flapjax.core
  (:use
    [hlisp.env :only [clone]])
  (:require [jayq.core :as jq]
            [jayq.util :as ju]))

(defn core-event [ev]
  (let [r (js/receiverE)]
    (-> (jq/$ "body")
      (.on ev (fn [v] (.sendEvent r v))))
    r))

(def *clicks*   (core-event "click"))
(def *changes*  (core-event "change"))

(def add-initfn! jq/$)

(defn id [elem]
  (peek (.-ids elem)))

(defn id! [elem]
  (if-not (seq (.-ids elem)) (clone elem) elem))

(defn filter-id [id]
  (fn [v]
    (< 0 (-> (jq/$ (.-target v))
           (.parentsUntil "body")
           (.andSelf)
           (.filter (str "[data-hl~='" id "']"))
           (.size)))))

(defn find-id [id]
  (jq/$ (str "[data-hl~='" id "']")))

(defn is-jq? [obj]
  (string? (.-jquery obj)))

(defn dom-get [elem]
  (find-id (id elem)))

(defn dom-attr!
  ([elem k]
   (jq/attr (dom-get elem) k))
  ([elem k v & kvs]
   (let [e (jq/attr (dom-get elem) k v)] 
     (when (seq kvs)
       (mapv (fn [[k v]] (jq/attr e k v)) (partition 2 kvs)))
     elem)))

(defn dom-remove-attr! [elem k & ks]
  (let [e (.removeAttr (dom-get elem) k)]
    (when (seq ks)
      (mapv #(.removeAttr e %) ks))
    elem))

(defn dom-add-class! [elem c & cs]
  (let [e (.addClass (dom-get elem) c)]
    (when (seq cs)
      (mapv #(.addClass e %) cs))
    elem))

(defn dom-remove-class! [elem c & cs]
  (let [e (.removeClass (dom-get elem) c)]
    (when (seq cs)
      (mapv #(.removeClass e %) cs))
    elem))

(defn dom-css!
  ([elem k v]
   (.css (dom-get elem) k v)
   elem)
  ([elem o]
   (let [ret (.css (dom-get elem) o)]
     (if (is-jq? ret) elem ret))))

(defn dom-toggle! [elem v]
  (.toggle (dom-get elem) v))

(defn init-e [rcv v]
  (add-initfn! #(.sendEvent rcv v))
  rcv)

(defn receiver-e [& init]
  (reduce init-e (js/receiverE) init))

(defn send-e [rcv v]
  (.sendEvent rcv v))

(defn sync-e [s1 s2]
  (let [e1 (js/filterRepeatsE s1)
        e2 (js/filterRepeatsE s2)]
    (js/mapE #(.sendEvent s2 %) e1)
    (js/mapE #(.sendEvent s1 %) e2)))

(defn filter-e [pred src]
  (js/filterE src pred))

(defn clicks-e [elem]
  (js/filterE *clicks* (filter-id (id elem))))

(defn changes-e [elem]
  (js/filterE *changes* (filter-id (id elem))))

(defn map-e [f a]
  (js/mapE f a))

