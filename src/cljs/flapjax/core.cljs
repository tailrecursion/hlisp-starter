(ns flapjax.core
  (:require F))

(declare receiverE sendE)

(def EventStream  F/EventStream)
(def Behavior     F/Behavior)

;;;;;;;;;;;;;;;;;;;;;;;;;;;  UTILITY FUNCTIONS  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn startsWith
  [streamE v]
  (.startsWith streamE v))

(defn changes
  [sourceB]
  (.changes sourceB))

;;;;;;;;;;;;;;;;;;;;;;;;;  EVENT STREAM FUNCTIONS  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def oneE   F/oneE)
(def zeroE  F/zeroE)
(def mapE   F/mapE)
(def mergeE F/mergeE)

(defn switchE
  [streamE]
  (.switchE streamE))

(defn filterE
  [streamE pred]
  (.filterE streamE pred))

(defn constantE
  [streamE v]
  (.constantE streamE v))

(defn collectE
  [streamE init f]
  (.collectE streamE init f))

(defn notE
  [streamE]
  (.notE streamE))

(defn filterRepeatsE
  [streamE]
  (.filterRepeatsE streamE))

(def receiverE F/receiverE)
(def sendEvent F/sendEvent)

(defn sendE
  [streamE v]
  (.sendEvent streamE v))

(defn snapshotE
  [streamE]
  (.snapshotE streamE))

(defn onceE
  [streamE]
  (.onceE streamE))

(defn skipFirstE
  [streamE]
  (.skipFirstE streamE))

(defn delayE
  [streamE intervalB]
  (.delayE streamE intervalB))

(defn blindE
  [streamE intervalB]
  (.blindE streamE intervalB))

(defn calmE
  [streamE intervalB]
  (.calmE streamE intervalB))

(def timerE F/timerE)

;;;;;;;;;;;;;;;;;;;;;;;;;;;  BEHAVIOR FUNCTIONS  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def constantB F/constantB)

(defn delayB
  [sourceB intervalB]
  (.delayB sourceB intervalB))

(defn valueNow
  [sourceB]
  (.valueNow sourceB))

(defn switchB
  [sourceBB]
  (.switchB sourceBB))

(def andB F.Behavior/andB)
(def orB  F.Behavior/orB)

(defn notB
  [valueB]
  (.notB valueB))

(def liftB F/liftB)
(def condB F/condB)

(defn ifB
  [predicateB consequentB alternativeB]
  (.ifB predicateB consequentB alternativeB))

(def timerB F/timerB)

(defn blindB
  [sourceB intervalB]
  (.blindB sourceB intervalB))

(defn calmB
  [sourceB intervalB]
  (.calmB sourceB intervalB))

