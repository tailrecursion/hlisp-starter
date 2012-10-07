# Starter

Here is the example `index.html` file (or look at the [demo](http://micha.github.com/hlisp-starter/)):

```html
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" />
  <title>hlisp test</title>

  <script type="text/hlisp">

    ;; Namespace declaration. Each page must have a unique namespace.
    ;; The ns macro is also pulling in functions defined in other name-
    ;; spaces, like the make-radio and make-tabs functions defined in
    ;; the mytest.ui namespace (src/cljs/mytest/ui.cljs).

    (ns holyshit
      (:use
        [flapjax.core :only [sync-e]]
        [mytest.ui    :only [make-radio make-tabs]])
      (:use-macros
        [hlisp.macros :only [def-values]]))

    ;; Create a "tabs" set with two trigger elements and two container
    ;; elements. When the trigger element is clicked the associated
    ;; container element is shown and all other containers are hidden.

    (def-values
      [mytabs questiontab question answertab answer]
      (make-tabs "one" li div "one" li div "two"))

    ;; Define a var to save a little typing.

    (def a-void (a {:href "javascript:void (0)"}))

    ;; Create a "radio button" set with two elements (anchors, in this
    ;; case). The myradio event stream carries values of "one" and "two"
    ;; when the first or second element is clicked on, respectively.

    (def-values
      [myradio showquestion showanswer]
      (make-radio "one" a-void "one" a-void "two"))

    ;; Keep myradio and mytabs synced: when one changes state the other
    ;; does, too.

    (sync-e myradio mytabs)

  </script>

  <style type="text/css">
    ul.control-tabs { padding: 0; list-style-type: none; }
    ul.control-tabs li { display: inline; padding-right: 5px; }
    ul.control-tabs li.checked { color: red; }
  </style>
</head>
<body>

  <ul class="control-tabs">
    <questiontab>question</questiontab>
    <answertab>answer</answertab>
  </ul>

  <div>
    <question>
      <p>
        <b>Q.</b> Why did the chicken cross the road? <showanswer>A</showanswer>
      </p>
    </question>
    <answer>
      <p>
        <b>A.</b> To get to the other side! <showquestion>Q</showquestion>
      </p>
    </answer>
  </div>

  <!--__HLISP__-->

</body>
</html>
```

# Rationale

The "hlisp" (everything needs a name) project aims to address perceived
problems confronting front-end user interface (UI) programmers. The goal is to
provide a basis for a robust development environment where complex structures
can be built by composing simpler structures such that the results can
themselves be composed to form even more complex things. A secondary goal is to
develop a more unified front-end code structure, to maximize the ability to
express the program functionally, and to minimize the complexity associated
with shared state and mutable data.

## Perceived Problems

I've started many projects with a clear view of the desired dynamic behaviors
and DOM structure, thinking to myself, "Ah, now I will leverage my experience
and this time I will do everything right. I will build a beautiful palace
of pure and clean code, and this time it won't balloon into a rigid morass
of imperative boilerplate." I begin work, and things seem to go well. But
little by little I always seem to find myself rewriting JavaScript code due
to seemingly trivial issues, such as differences in the DOM structure between
components. Every time this happens I ruminate on my predicament, refactor,
and continue&mdash;ultimately unsatisfied with the results.

In a large project there always seems to be a tipping point where I
lose control of the code and start to consider more pragmatic git-r-dun
compromises. At this point I begin to realize that minor mistakes
in architecture have become difficult to fix, which results in further
compromises and code bloat.

Why does this happen? The rest of this section lists some possible causes.

### Primitives, Abstraction, and Composition

Front-end programs generally involve a number of disparate domains: there
is the HTML markup itself, CSS rules, JavaScript programs and libraries,
templating tools, and preprocessors. Sometimes the HTML markup itself is
rendered on the server by a program written in yet another language. There
is no unified environment with common primitives, means of abstraction,
and means of composition.

This presents the programmer with an extremely difficult design problem when
he is developing the architecture of his program. The slightest misstep when
designing the underlying architecture might have ramifications in other
domains, and the fix could pose issues with interoperability betweeen the
domains. This causes the program to become rigid and brittle. Changes tend
to have exponentially expanding consequences at a certain level of complexity.

### Shared State &amp; Side-Effects

Dynamic behaviors in JavaScript generally use callbacks to manipulate mutable
DOM elements. These callbacks are, necessarily, of void type (aside from
certain cases where boolean false is returned to produce a side-effect),
and the stream of events that will trigger the callbacks are not first-class
entities in their own right.

Since callbacks are of void type they can't be composed and must perform their
job by imperatively mutating shared state. The lack of an abstraction
encompassing the event stream itself only exacerbates the problems inherent in
the callback system, and prevents more functional composition of the event
streams (a prerequisite for minimization of shared state).

## Goals

So which properties should the proposed solution have?

* **Don't throw away the HTML markup.** Designers and creative types can't be
  expected to be highly-skilled, talented programmers. Discarding HTML in
  favor of a programming language will necessarily introduce another interface
  with all of the accompanying complexity. Instead, the solution should ideally
  be one which exposes powerful abstractions to the designer in a way that they
  can reasonably be expected to understand and use to their advantage.
* **Facilitate a modular structure.** Global references should be eliminated in
  favor of namespaced, lexical variables. Selector-based linkages between the
  DOM and the program should be unnecessary.
* **Provide uniform means of composition.** All abstractions should be
  uniformly composeable, first-class entities.
* **Use the means of composition provided by the DOM.** Since front-end work
  ultimately results in DOM structure and manipulation, and since HTML
  markup is to be a core component, the solution should provide a means of
  abstraction compatible with that of the DOM. That is, via tree-like append.
* **Provide a first-class abstraction for event streams.** Callback driven DOM
  manipulation tightly couples DOM structure to event processing code, and
  dictates a pervasive reliance on side-effects and shared state. First-class
  event streams allows a more functional approach.

# Development

Install [Leiningen2](https://github.com/technomancy/leiningen)

Install dependencies:

    lein deps

Start a server to serve the app on port 4000:

    script/serve

In another tab, you can start watcher-based ClojureScript compilation:

    script/autobuild

Visit [http://localhost:4000/](http://localhost:4000/) to see the app.

## Running a ClojureScript REPL

The ClojureScript REPL compiles ClojureScript forms into JavaScript locally
and sends them to the browser to be evaluated.

### Emacs

* `C-x d` to the `cljs-starter` directory
* `M-x set-variable`, and set the variable `inferior-lisp-program` to
  `script/cljsrepl`
* `M-x run-lisp` and you should see an inferior-lisp buffer running a
  ClojureScript REPL.
* Visit or reload [http://localhost:4000/](http://localhost:4000/) in
  the browser.

### Vi

* If you know of a good inferior lisp setup for vi I'd like to hear about it!

### Example Session

* Hlisp defines vars for all the DOM elements in the following namespaces:
  * Project HTML page namespaces.
  * The `hlisp.env` namespace.
* Hlisp nodes in the REPL are printed as lists.
* The REPL also prints hlisp nodes in the browser console as DOM elements.

```
ClojureScript:cljs.user> (in-ns 'hlisp.env)

ClojureScript:hlisp.env> (div {:id "main"} (h1 ($text "Hello, world!")) (p ($text "How do you do?")))
(div {:id "main"} (h1 ($text "Hello, world!")) (p ($text "How do you do?")))

ClojureScript:hlisp.env> (defn f [x] (div {:class "foo"} x))
#<function f(x){ ... }>

ClojureScript:hlisp.env> (f (p ($text "I am wrapped in a div?")))
(div {:class "foo"} (p ($text "I am wrapped in a div?")))

ClojureScript:hlisp.env> (def test1 (f (label ($text "Name:"))))
(div {:class "foo"} (label ($text "Name:")))

ClojureScript:hlisp.env> (test1 br (input {:type "text"}))
(div {:class "foo"} (label ($text "Name:")) br (input {:type "text"}))
```

## Configuration

Have a look at the `project.clj` file:

```clj
;; ---------------------------------------------------------------------------
;; Leiningen Plugins
;; ---------------------------------------------------------------------------
;;
;; 1. lein-cljsbuild    To get a ClojureScript REPL going.
;; 2. lein-ring         To get a local dev server going.
;; 3. lein-hlisp        The hlisp compiler---produces main.js.
;; 
;; ---------------------------------------------------------------------------
;; Dependencies
;; ---------------------------------------------------------------------------
;;
;; 1. clojure           Goes without saying.
;; 2. compojure         For the dev server setup.
;; 3. hlisp-macros      Some handy Clojure macros to use in cljs.
;;
;; ---------------------------------------------------------------------------
;; Hlisp Configuration
;; ---------------------------------------------------------------------------
;;
;; 1. html-src          Directory containing the HTML source files.
;; 2. cljs-src          Directory containing cljs source files.
;; 3. html-out          Directory into which processed HTML and JS will go.
;; 4. base-dir          URL path of deployed HTML if HTML is not served from 
;;                      the document root.
;; 5. includes          Vector of JS files to prepend (in order) to main.js.
;; 6. cljsc-opts        ClojureScript compiler options.

(defproject
  hlisp-starter "0.1.0-SNAPSHOT"
  :description  "Example hlisp project."
  :url          "https://github.com/micha/hlisp-starter"
  :license      {:name "Eclipse Public License"
                 :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :plugins      [[lein-cljsbuild "0.2.7"]
                 [lein-ring "0.7.1"]
                 [lein-hlisp "0.1.0-SNAPSHOT"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.0.4"] 
                 [hlisp-macros "0.1.0-SNAPSHOT"]]
  :eval-in      :leiningen
  :ring         {:handler example.routes/app}
  :cljsbuild    {:builds []}
  :hlisp        {:html-src    "src/html"
                 :cljs-src    "src/cljs"
                 :html-out    "resources/public"
                 :base-dir    ""
                 :includes    ["src/jslib/jquery.js"
                               "src/jslib/flapjax.js"]
                 :cljsc-opts  {:optimizations  :whitespace
                               :externs        ["src/extern/jquery.js"
                                                "src/extern/flapjax.js"]}})
```


