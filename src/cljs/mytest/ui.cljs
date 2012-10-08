(ns mytest.ui
  (:use
    [flapjax.core :only [id! receiver-e clicks-e map-e send-e filter-e
                         dom-attr! dom-add-class! dom-remove-attr!
                         dom-remove-class! dom-css! dom-toggle!]]))

(declare add-radio add-deck add-tab)

(defn make-radio
  "Creates a radio button type relationship between elements. A radio button
  is, abstractly, an event stream carrying the current value of the radio and
  a number of trigger elements and associated values. When the user clicks on
  one of the trigger elements the current value of the radio changes to that
  of the element, and the event is sent.
  
  This function takes an initial value and a number of alternating trigger
  elements and values, and returns a list containing the event stream and the
  modified trigger elements."
  [init & args]
  (let [r (receiver-e init)]
    (list* r (map (fn [[elem v]] (add-radio r elem v))
                  (partition 2 args)))))

(defn add-radio [radio elem v]
  (let [elem (id! elem)]
    (map-e (fn [_] (send-e radio v)) (clicks-e elem)) 
    (map-e
      #(if (not= % v)
         (-> elem
           (dom-remove-attr! "data-checked") 
           (dom-css! "cursor" "pointer") 
           (dom-remove-class! "checked")) 
         (-> elem
           (dom-attr! "data-checked" "data-checked")
           (dom-css! "cursor" "default")
           (dom-add-class! "checked")))
      radio) 
    elem))

(defn make-deck [init & args]
  (let [r (receiver-e init)]
    (list* r (map (fn [[elem v]] (add-deck r elem v))
                  (partition 2 args)))))

(defn add-deck [deck elem v]
  (let [elem (id! elem)]
    (map-e #(dom-toggle! elem (= % v)) deck) 
    elem))

(defn add-tab [tabs trig cont v]
  (let [t (add-radio tabs trig v)
        c (add-deck tabs cont v)]
    (list t c)))

(defn make-tabs [init & args]
  (let [r (receiver-e init)]
    (list* r (mapcat (fn [[trig cont v]] (add-tab r trig cont v))
                     (partition 3 args)))))
