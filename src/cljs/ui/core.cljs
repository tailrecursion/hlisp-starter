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
    [flapjax.dom :only [id! add-class!]]))

(defn make-binary-state
  [dflE adjective state-true state-false]  
  (fn binary
    ([e]
     (let [e (id! e)]
       (binary e (dflE e))))
    ([e streamE]
     (let [e (id! e)]
       (-> e
         (domAddClass (initE adjective))
         (domAttr (consE (constantE streamE (str "data-" state-true)) streamE))
         (domToggleClass (consE (constantE streamE state-true) streamE))
         (domToggleClass (consE (constantE streamE state-false) (notE streamE))))))))

(def make-hoverable
  "Hoverable elements have the 'hover' class on them whenever the mouse is
  over them."
  (make-binary-state onHoverE "hoverable" "hover" "not-hover"))

(def make-clickable
  "Clickable elements have the 'active' class applied whenever the mouse is
  pressed on them."
  (make-binary-state onActiveE "clickable" "active" "not-active"))

(def make-selectable
  "Selectable elements..."
  (make-binary-state onClickE "selectable" "selected" "not-selected"))

(def make-disableable
  "Disableable elements..."
  (make-binary-state zeroE "disableable" "disabled" "not-disabled"))

(def make-checkable
  "Checkable elements..."
  (make-binary-state onClickE "checkable" "checked" "not-checked"))

(defn make-checkbox
  "A checkbox is..."
  [e]
  (let [checkE    (receiverE)
        checkedB  (E->B checkE ::nil)
        e         (-> e
                    (make-checkable checkE)
                    make-clickable
                    make-hoverable)]
    (mapE #(sendE checkE ((if % not identity) (valueNow checkedB)))
          (onClickE e))
    {:elem      e
     :checkE    checkE
     :checkedB  checkedB}))

(defn make-deck
  "A deck is..."
  [& optvals]
  (let [[options values] (apply map vector (partition 2 optvals))
        selectE   (receiverE)
        selectedB (E->B selectE ::nil)
        wireup    (fn [e v] (make-selectable e (mapE #(= v %) selectE)))]
    {:selectE   selectE
     :selectedB selectedB
     :options   (mapv wireup options values)
     :values    values}))

(defn make-select
  "A select is..."
  [& optvals]
  (let [deck    (apply make-deck optvals)
        wireup  (fn [e v]
                  (mapE #(sendE (:selectE deck) v) (onClickE e))
                  (-> e make-clickable make-hoverable))]
    (assoc deck :options (mapv wireup (:options deck) (:values deck)))))

(defn make-restricted-select
  [select]
  (let [restrictE (receiverE)]
    (mapv (fn [e v] (make-disableable e (mapE #(contains? % v) restrictE)))
          (:options select)
          (:values select))
    (assoc select :restrictE restrictE :restrictedB (E->B restrictE #{}))))

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

(defn make-linked-selects
  [n & optvals]
  (let [selects (mapv #(apply make-select optvals) (range 0 n))]
    (apply link-unique-selects (mapv make-restricted-select selects))))

(defn make-tabs
  [& optvals]
  (let [[options containers values] (apply map vector (partition 3 optvals))
        select  (apply make-select (interleave options values))
        deck    (apply make-deck (interleave containers values))]
    (mapE #(sendE (:selectE deck) %) (B->E (:selectedB select)))
    (assoc select :containers (:options deck))))
