(ns ui.core
  (:use
    [flapjax.core   :only [oneE zeroE mapE mergeE switchE filterE constantE
                           collectE notE filterRepeatsE receiverE sendE
                           snapshotE onceE skipFirstE delayE blindE calmE
                           timerE valueNow switchB andB orB notB liftB condB
                           ifB timerB blindB calmB]] 
    [flapjax.hlisp  :only [isE? isB? E->B B->E initE doE consE applyE caseE
                           constantB delayB consB applyB compE onClickE
                           onChangeE onMouseDownE onMouseUpE onMouseOverE
                           onMouseOutE onHoverE onActiveE domAttr domRemoveAttr
                           domCss domAddClass domRemoveClass domToggleClass
                           domToggle]]
    [flapjax.dom :only [id!]]))

(defn make-hoverable
  "Hoverable elements have the 'hover' class on them whenever the mouse is
  over them."
  [e hoverE]
  (let [e (id! e)]
    (domToggleClass e (consE (constantE hoverE "hover") hoverE))))

(defn make-clickable
  "Clickable elements have the 'active' class applied whenever the mouse is
  pressed on them."
  [e activeE]
  (let [e (id! e)]
    (domToggleClass e (consE (constantE activeE "active") activeE))))

(defn make-selectable
  "Selectable elements..."
  [e selectE]
  (let [e (id! e)]
    (domToggleClass e (consE (constantE selectE "selected") selectE))))

(defn make-disableable
  "Disableable elements..."
  [e disableE]
  (let [e (id! e)]
    (-> e
      (domToggleClass (consE (constantE disableE "disabled") disableE))
      (domAttr (consE (constantE disableE "disabled") disableE)))))

(defn make-select
  [& optvals]
  (let [[options values] (apply map vector (partition 2 optvals)) 
        default   (first values)
        selectE   (receiverE)
        selectedB (E->B selectE default)]
    (letfn [(wireup [e v]
              (let [e (id! e)]
                (mapE #(sendE selectE v) (onClickE e)) 
                (-> e
                  (make-clickable (onActiveE e))
                  (make-hoverable (onHoverE e))
                  (make-selectable (mapE #(= v %) selectE)))))]
      {:selectE   (initE selectE default)
       :selectedB selectedB
       :options   (mapv wireup options values)
       :values    values})))

(defn make-restricted-select
  [select]
  (let [restrictE (receiverE)]
    (mapv (fn [e v] (make-disableable e (mapE #(contains? % v) restrictE)))
          (:options select)
          (:values select))
    (-> select
      (assoc :restrictE restrictE)
      (assoc :restrictedB (E->B restrictE #{})))))

(defn link-unique-selects
  [& selects]
  (let [linkedB (apply consB (mapv :selectedB selects))
        linkedE (receiverE)
        rset    #(set (concat (take %1 %2) (drop (inc %1) %2)))]
    (doall
      (map-indexed
        (fn [i e]
          (mapE #(sendE (:selectE e) (nth % i)) linkedE)
          (mapE #(sendE (:restrictE e) (rset i %)) (B->E linkedB)))
        selects)) 
    {:linkedE linkedE
     :linkedB linkedB
     :selects selects}))
