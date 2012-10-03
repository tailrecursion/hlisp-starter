(ns jayq.core
  (:refer-clojure :exclude [val empty remove find])
  (:require [clojure.string :as string])
  (:use [jayq.util :only [clj->js]]))

(defn crate-meta [func]
  (.-prototype._crateGroup func))

(defn ->selector [sel]
  (cond
    (string? sel) sel
    (fn? sel) (if-let [cm (crate-meta sel)]
                (str "[crateGroup=" cm "]")
                sel)
    (keyword? sel) (name sel)
    :else sel))

(def $ (fn [sel & [context]]
         (if-not context
           (js/jQuery (->selector sel))
           (js/jQuery (->selector sel) context))))

(extend-type js/jQuery
  ISeqable
  (-seq [this] (when (.get this 0)
                 this))
  ISeq
  (-first [this] (.get this 0))
  (-rest [this] (if (> (count this) 1)
                  (.slice this 1)
                  (list)))

  ICounted
  (-count [this] (. this (size)))

  IIndexed
  (-nth [this n]
    (when (< n (count this))
      (.slice this n (inc n))))
  (-nth [this n not-found]
    (if (< n (count this))
      (.slice this n (inc n))
      (if (undefined? not-found)
        nil
        not-found)))

  ISequential

  ILookup
  (-lookup
    ([this k]
       (or (.slice this k (inc k)) nil))
    ([this k not-found]
       (-nth this k not-found)))

  IReduce
  (-reduce [this f]
    (ci-reduce this f))
  (-reduce [this f start]
    (ci-reduce this f start)))

(set! jQuery.prototype.call
      (fn
        ([_ k] (-lookup (js* "this") k))
        ([_ k not-found] (-lookup (js* "this") k not-found))))

(defn anim [elem props dur]
  (.animate elem (clj->js props) dur))

(defn text
  ([$elem]
   (.text $elem))
  ([$elem txt]
    (.text $elem txt)))

(defn css [$elem opts]
  (if (keyword? opts)
    (.css $elem (name opts))
    (.css $elem (clj->js opts))))

(defn attr [$elem a & [v]]
  (if (map? a)
    (. $elem (attr (clj->js a)))
    (let [a (name a)]
      (if-not v
        (. $elem (attr a))
        (. $elem (attr a v))))))

(defn remove-attr [$elem a]
  (.removeAttr $elem (name a)))

(defn data [$elem k & [v]]
  (let [k (name k)]
    (if-not v
      (. $elem (data k))
      (. $elem (data k v)))))

(defn position [$elem]
  (js->clj (.position $elem) :keywordize-keys true))

(defn add-class [$elem cl]
  (let [cl (name cl)]
    (.addClass $elem cl)))

(defn remove-class [$elem cl]
  (let [cl (name cl)]
    (.removeClass $elem cl)))

(defn toggle-class [$elem cl]
  (let [cl (name cl)]
    (.toggleClass $elem cl)))

(defn has-class [$elem cl]
  (let [cl (name cl)]
    (.hasClass $elem cl)))

(defn after [$elem content]
  (.after $elem content))

(defn before [$elem content]
  (.before $elem content))

(defn append [$elem content]
  (if (vector? content) 
    (.append $elem (clj->js content))
    (.append $elem content)))

(defn prepend [$elem content]
  (.prepend $elem content))

(defn remove [$elem]
  (.remove $elem))

(defn hide [$elem & [speed on-finish]]
  (.hide $elem speed on-finish))

(defn show [$elem & [speed on-finish]]
  (.show $elem speed on-finish))

(defn toggle [$elem & [speed on-finish]]
  (.toggle $elem speed on-finish))

(defn fade-out [$elem & [speed on-finish]]
  (.fadeOut $elem speed on-finish))

(defn fade-in [$elem & [speed on-finish]]
  (.fadeIn $elem speed on-finish))

(defn slide-up [$elem & [speed on-finish]]
  (.slideUp $elem speed on-finish))

(defn slide-down [$elem & [speed on-finish]]
  (.slideDown $elem speed on-finish))

(defn parent [$elem]
  (.parent $elem))

(defn find [$elem selector]
  (.find $elem (name selector)))

(defn closest [$elem selector & [context]]
  (.closest $elem selector context))

(defn clone [$elem]
  (.clone $elem))

(defn inner [$elem v]
  (.html $elem v))

(defn empty [$elem]
  (.empty $elem))

(defn val [$elem & [v]]
  (if v
    (.val $elem v)
    (. $elem (val))))

(defn serialize [$elem]
  (.serialize $elem))

(defn queue [$elem callback]
  (. $elem (queue callback)))

(defn dequeue [elem]
  (. ($ elem) (dequeue)))

(defn document-ready [func]
  (.ready ($ js/document) func))

(defn xhr [[method uri] content callback]
  (let [params (clj->js {:type (string/upper-case (name method))
                         :data (clj->js content)
                         :success callback})]
    (.ajax js/jQuery uri params)))

(defn ajax
  ([url settings]
    (.ajax js/jQuery url (clj->js settings)))
  ([settings]
    (.ajax js/jQuery (clj->js settings))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Events
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn bind [$elem ev func]
  (.bind $elem (name ev) func))

(defn unbind [$elem ev & [func]]
  (.unbind $elem (name ev) func))

(defn trigger [$elem ev]
  (.trigger $elem (name ev)))

(defn delegate [$elem sel ev func]
  (.delegate $elem (->selector sel) (name ev) func))

(defn ->event [e]
  (cond
    (keyword? e) (name e)
    (map? e) (clj->js e)
    (coll? e) (string/join " " (map name e))
    :else (throw (js/Error. (str "Unknown event type: " e)))))

(defn on [$elem events & [sel data handler]]
  (.on $elem
       (->event events)
       (->selector sel)
       data
       handler))

(defn one [$elem events & [sel data handler]]
  (.one $elem
        (->event events)
        (->selector sel)
        data
        handler))

(defn off [$elem events & [sel handler]]
  (.off $elem
        (->event events)
        (->selector sel)
        handler))

(defn prevent [e]
  (.preventDefault e))
