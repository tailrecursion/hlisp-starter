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
      (:require-macros
        [mytest.macros :as m]))

    ;; Create a "tabs" set with two trigger elements and two container
    ;; elements. When the trigger element is clicked the associated
    ;; container element is shown and all other containers are hidden.

    (m/def-values
      [mytabs questiontab question answertab answer]
      (make-tabs "one" li div "one" li div "two"))

    ;; Define a var to save a little typing.

    (def a-void (a {:href "javascript:void (0)"}))

    ;; Create a "radio button" set with two elements (anchors, in this
    ;; case). The myradio event stream carries values of "one" and "two"
    ;; when the first or second element is clicked on, respectively.

    (m/def-values
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

### Emacs

* `C-x d` to the `cljs-starter` directory
* `M-x set-variable`, and set the variable `inferior-lisp-program` to `script/cljsrepl`
* `M-x run-lisp` and you should see an inferior-lisp buffer running a ClojureScript REPL.  Then, visit [http://localhost:4000/](http://localhost:4000/).

### Vi

* If you know of a good inferior lisp setup for vi I'd like to hear about it!

### Example Session

* Hlisp defines vars for all the DOM elements in the following namespaces:
  * Project HTML page namespaces.
  * The `hlisp.env` namespace.
* DOM elements in the REPL are printed in the browser JavaScript console, as
  well as in the REPL itself.

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

## API

The [hlisp.env](https://github.com/micha/lein-hlisp/blob/master/resources/env.cljs)
namespace contains the functions and types used.

### DOM Elements

div, span, p, etc.

```
All HTML tags are defined in the hlisp.env namespace and also in any HTML page
namespace.
```

### make-elem-node

(make-elem-node tag) <br>
(make-elem-node tag attrs) <br>
(make-elem-node tag attrs children) <br>
(make-elem-node tag attrs children ids)

```
Returns a new element node with the given initial state.
```

### make-text-node

(make-text-node text)

```
Creates a new text node with given text.
```

### make-comment-node

(make-comment-node text)

```
Creates a new comment node with given text.
```

### tag

(tag node)

```
Returns the node name (i.e. the HTML tag) of node.
```

### attrs

(attrs node)

```
Returns the attribute map of node.
```

### branch?

(branch? node)

```
Returns true if node can have children.
```

### children

(children node)

```
Returns a seq of child nodes of node.
```

### make-node

(make-node node children)

```
Given a node node and a seq of children children, make a new node.
```

### dom

(dom node)

```
Returns DOM element for node (and children).
```

### node-zip

(node-zip node)

```
Returns a clojure.zip/zipper structure for node.
```

## Configuration

Have a look at the `project.clj` file:

```clj
(defproject
  hlisp-starter "0.1.0-SNAPSHOT"
  :description  "Example hlisp project."
  :url          "https://github.com/micha/hlisp-starter"
  :license      {:name "Eclipse Public License"
                 :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :plugins      [[lein-hlisp "0.1.0-SNAPSHOT"]
                 [lein-cljsbuild "0.2.4"]
                 [lein-ring "0.7.1"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.0.4"]]
  :eval-in      :leiningen
  :ring         {:handler example.routes/app}
  :cljsbuild    {:builds []}
  :hlisp        {:html-src    "src/html"
                 :cljs-src    "src/cljs"
                 :html-out    "resources/public"
                 :includes    ["src/jslib/jquery.js"]
                 :cljsc-opts  {:optimizations  :whitespace
                               :externs        ["src/extern/jquery.js"]}} 

  )
```


