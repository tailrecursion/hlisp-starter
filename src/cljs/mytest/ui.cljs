(ns mytest.ui
  (:use
    [flapjax.core :only [receiver-e clicks-e map-e send-e filter-e
                         dom-attr! dom-add-class! dom-remove-attr!
                         dom-remove-class! dom-css! dom-toggle!]]))

(defn add-radio [radio elem v]
  (map-e (fn [_] (send-e radio v)) (clicks-e elem))
  (map-e
    (fn [_]
      (-> elem
        (dom-remove-attr! "data-checked") 
        (dom-css! "cursor" "pointer") 
        (dom-remove-class! "checked"))) 
    (filter-e #(not= % v) radio))
  (map-e
    (fn [_]
      (-> elem
        (dom-attr! "data-checked" "data-checked")
        (dom-css! "cursor" "default")
        (dom-add-class! "checked")))
    (filter-e #(= % v) radio))
  elem)

(defn make-radio [init & args]
  (let [r (receiver-e init)]
    (list* r (map (fn [[elem v]] (add-radio r elem v))
                  (partition 2 args)))))

(defn make-deck [init]
  (receiver-e init))

(defn add-deck [deck elem v]
  (map-e #(dom-toggle! elem (= % v)) deck)
  elem)

(defn add-tab [tabs trig cont v]
  (let [t (add-radio tabs trig v)
        c (add-deck tabs cont v)]
    (list t c)))

(defn make-tabs [init & args]
  (let [r (receiver-e init)]
    (list* r (mapcat (fn [[trig cont v]] (add-tab r trig cont v))
                     (partition 3 args)))))
