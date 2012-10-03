(ns mytest.macros)

(defmacro def-values
  "Destructuring def, similar to scheme's define-values."
  [bindings values]
  (->>
    (macroexpand `(let [~bindings ~values]))
    (second)
    (partition 2)
    (map #(cons 'def %))
    (list* 'do)))

(comment
  
  ;=> (macroexpand '(def-values [x y z] [1 2 3]))
  (do
    (def vec__803 [1 2 3])
    (def x (clojure.core/nth vec__803 0 nil))
    (def y (clojure.core/nth vec__803 1 nil))
    (def z (clojure.core/nth vec__803 2 nil)))

  )
