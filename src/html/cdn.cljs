(html
 (head
  (meta {:charset "utf-8"})
  (title "hlisp cdn solution"))
 (body
  (ns hlisp.demo.cdn
    (:use
     [ui.core :only [make-linked-selects]]
     [flapjax.core :only [mapE skipFirstE]]
     [flapjax.hlisp :only [initE B->E]]
     [flapjax.core   :only [oneE zeroE mapE mergeE switchE filterE constantE
                            collectE notE filterRepeatsE receiverE sendE
                            snapshotE onceE skipFirstE delayE blindE calmE
                            timerE valueNow switchB andB orB notB liftB condB
                            ifB timerB blindB calmB]] 
     [flapjax.hlisp  :only [isE? isB? E->B B->E initE doE consE applyE caseE
                            constantB delayB consB applyB compE onClickE
                            onChangeE onMouseDownE onMouseUpE onMouseOverE
                            onMouseOutE onHoverE onActiveE domValueB domAttr domRemoveAttr
                            domCss domAddClass domRemoveClass domToggleClass
                            domToggle]]
     [flapjax.dom :only [id! value!]])
    (:use-macros
     [hlisp.macros :only [def-values]]))

  (def response1 [["http://blah.com/broken-image.png"]
                  ["http://blah.com/broken-image.png" "http://tailrecursion.com/~alan/pix/froghat.jpg"]
                  ["http://tailrecursion.com/~alan/pix/geysercar.jpg" "http://tailrecursion.com/~alan/pix/geysercar.jpg"]
                  ["http://tailrecursion.com/~alan/pix/ohthankgod.jpg"]])
  
  (def response2 nil)

  (def default-image "http://1.bp.blogspot.com/-CUoBpccetO0/UBftdwGsP1I/AAAAAAAABjw/6uFqr6NfXsg/s1600/imageNotFound.png")

  (defn make-input
    [elem]
    (let [e (id! elem)]
      {:element e
       :behavior (domValueB e)}))

  (let [input-object (make-input input)]
    (def search-input (:element input-object))
    (def search-b (:behavior input-object)))

  ;; We're not sifting through the DOM as data, so we need an hlisp
  ;; construct to represent where images are ultimately dumped.
  (def image-container div)

  (defn make-clickable-element
    [elem]
    (let [e (id! elem)]
      {:element e
       :estream (onClickE e)}))

  (let [search-button-obj (make-clickable-element input)]
    (def search-button (:element search-button-obj))
    (def search-e (:estream search-button-obj)))

  (defn show-images [tag]
    (js/console.log tag))

  (mapE show-images )

  (div
   (h1 "Image Search")
   hr
   (search-input {:type "text"})
   (search-button {:type "submit" :value "go"})
   (image-container {:id "image-container"}
                    img1
                    img2
                    img3
                    img4))))