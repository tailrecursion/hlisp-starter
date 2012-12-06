(ns hlisp.test.index2
  (:use
    [ui.core :only [make-tabs make-linked-selects make-checkbox]]
    [flapjax.core :only [mapE skipFirstE]]
    [flapjax.hlisp :only [initE B->E]])
  (:use-macros
    [hlisp.macros :only [def-values]]))

(def c1 (make-checkbox span))
(def l1 (make-linked-selects 2 span "one" span "two" span "three")) 
(def t1 (make-tabs li div "1" li div "2" li div "3"))

(def check1 (:elem c1))
(def-values
  [[s1r1 s1r2 s1r3] [s2r1 s2r2 s2r3]]
  (mapv :options (:selects l1))) 
(def-values
  [[tabs-t1 tabs-t2 tabs-t3] [tabs-c1 tabs-c2 tabs-c3]]
  [(:options t1) (:containers t1)])

(initE (:checkE c1) true)
(initE (:linkedE l1) ["one" "two"]) 
(initE (:selectE t1) "1")

(mapE #(js/console.log (str %)) (skipFirstE (B->E (:linkedB l1))))

(html
  (head
    (meta {:charset "utf-8"})
    (title "hlisp test 2")
    (style {:type "text/css"}
       "
        .clickable { cursor: pointer; }
        .checkbox.hover { text-decoration: underline; }
        .checkbox.checked { color: red; }
        .linked-selects .active,
        .linked-selects .active.hover { color: red; }
        .linked-selects .hover { color: blue; text-decoration: underline; }
        .linked-selects .selected { color: green; }
        .linked-selects .disabled { cursor: default; text-decoration: line-through; }
        .tab-control .selected { text-decoration: underline; }
        .tab-content .not-selected { display: none; }"))

  (body
    (h1 "Hello world")
    (p "This is a test.")

    (check1 {:class "checkbox"} "Click me!")

    hr

    (ul {:class "tab-control"}
      (tabs-t1 "tab 1")
      (tabs-t2 "tab 2")
      (tabs-t3 "tab 3"))

    (div {:class "tab-content"}
      (tabs-c1
        (h3 "Tab 1")
        (p "Content for tab 1."))
      (tabs-c2
        (h3 "Tab 2")
        (p "Content for tab 2."))
      (tabs-c3
        (h3 "Tab 3")
        (p "Content for tab 3.")))

    hr

    (table {:class "linked-selects"}
      (tr
        (td (s1r1 "doit 1"))
        (td (s1r2 "doit 2"))
        (td (s1r3 "doit 2")))
      (tr
        (td (s2r1 "doit 1"))
        (td (s2r2 "doit 2"))
        (td (s2r3 "doit 2"))))

    hr))
