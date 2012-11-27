(ns flapjax.hlisp
  (:require
    [flapjax.core :as F]
    [flapjax.dom  :as dom]
    [jayq.core    :as jq]))

(declare distinctE)

;;;;;;;;;;;;;;;;;;;;;;;;;;;  UTILITY FUNCTIONS  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn isE?
  [stream]
  (= (type stream) F/EventStream))

(defn isB?
  [stream]
  (= (type stream) F/Behavior))

(defn E->B
  ([streamE]
   (E->B streamE nil))
  ([streamE v] 
   (F/startsWith (distinctE streamE) v)))

(defn B->E
  [valueB]
  (distinctE (F/changes valueB)))

;;;;;;;;;;;;;;;;;;;;;;;;;  EVENT STREAM FUNCTIONS  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def sendE F/sendEvent)

(defn distinctE
  [streamE]
  (F/mapE
    first
    (F/filterE
      (F/collectE streamE [::nil ::nil] #(vector %1 (if (= %1 (first %2)) ::nil %1)))
      #(not= ::nil (second %)))))

(defn initE
  ([v]
   (initE (F/receiverE) v))
  ([streamE v] 
   (jq/$ #(sendE streamE v)) 
   streamE))

(defn doE
  [streamE f]
  (F/mapE (fn [v] (f v) v) streamE))

(defn consE
  [& streamEs]
  (B->E (apply consB (mapv E->B streamEs))))

(defn applyE
  [streamE f]
  (F/mapE (partial apply f) streamE))

(defn caseE
  [& streamvals]
  (apply F/mergeE (mapv (partial apply F/constantE) (partition 2 streamvals))))

(defn compE
  [streamE1 streamE2]
   (F/mapE (partial sendE streamE1) streamE2))

;;;;;;;;;;;;;;;;;;;;;;;;;;;  BEHAVIOR FUNCTIONS  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn consB
  [& sourceBs]
  (apply F/liftB vector sourceBs))

(defn applyB
  [f & argBs]
  (apply F/liftB (partial apply f) argBs))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;  CORE EVENTS  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn core-event
  [event]
  (let [r (F/receiverE)]
    (->
      (jq/$ "body")
      (.on event (partial sendE r)))
    r))

(def *click*      (core-event "click"))
(def *change*     (core-event "change"))
(def *mousedown*  (core-event "mousedown"))
(def *mouseup*    (core-event "mouseup"))
(def *mouseover*  (core-event "mouseover"))
(def *mouseout*   (core-event "mouseout"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;  NOZZLES  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn onE
  [event e]
  (-> event
    (F/filterE (dom/filter-id (dom/id e)))
    (F/filterE dom/filter-not-disabled)))

(def onClickE      (partial onE *click*))
(def onChangeE     (partial onE *change*))
(def onMouseDownE  (partial onE *mousedown*))
(def onMouseUpE    (partial onE *mouseup*))
(def onMouseOverE  (partial onE *mouseover*))
(def onMouseOutE   (partial onE *mouseout*))

(defn onHoverE
  [e]
  (let [ins   (onMouseOverE e)
        outs  (onMouseOutE  e)]
    (caseE ins true outs false)))

(defn onActiveE
  [e]
  (let [downs (onMouseDownE e)
        ups   (F/mergeE (onMouseOutE e) *mouseup*)]
    (caseE downs true ups false)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;  SPIGOTS  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn dotoDom
  [f]
  (fn [e streamE]
    (doE streamE (partial f e))
    e))

(def domAttr          (dotoDom (partial apply dom/attr!)))
(def domRemoveAttr    (dotoDom (partial apply dom/remove-attr!)))
(def domCss           (dotoDom dom/css!))
(def domAddClass      (dotoDom dom/add-class!))
(def domRemoveClass   (dotoDom dom/remove-class!))
(def domToggleClass   (dotoDom (partial apply dom/toggle-class!)))
(def domToggle        (dotoDom dom/toggle!))

