# Starter

Here is the example `index.html` file (or look at the [demo](http://micha.github.com/hlisp-starter/)):

```html
<!DOCTYPE html>
<head>
  <meta charset="utf-8" />
  <title>hlisp test</title>

  <script type="text/hlisp">

    ;; Namespace declaration. Each page must have a unique namespace.

    (ns holyshit
      (:use
        [hlisp.env    :only [clone]]
        [flapjax.core :only [sync-e]]
        [mytest.ui    :only [make-radio make-tabs]])
      (:require-macros
        [mytest.macros :as m]))

    ;; Create a "tabs" set with two trigger elements and two container
    ;; elements. When the trigger element is clicked the associated
    ;; container element is shown and all other containers are hidden.

    (m/def-values
      [mytabs questiontab question answertab answer]
      (make-tabs "one"
        (clone li) (clone div) "one"
        (clone li) (clone div) "two"))

    ;; Define a var to save a little typing.

    (def a-void (a {:href "javascript:void (0)"}))

    ;; Create a "radio button" set with two elements (anchors, in this
    ;; case). The myradio event stream carries values of "one" and "two"
    ;; when the first or second element is clicked on, respectively.

    (m/def-values
      [myradio showquestion showanswer]
      (make-radio "one"
        (clone a-void) "one"
        (clone a-void) "two"))

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

  <!--This here is a comment.-->

  <div>
    <question id="foo">
      <p>
        <b>Q.</b> Why did the chicken cross the road? <showanswer>A</showanswer>
      </p>
    </question>
    <answer id="bar">
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
* Hlisp elements print both in the REPL and in the browser console.

```
ClojureScript:cljs.user> (in-ns 'hlisp.env)

ClojureScript:hlisp.env> (div {:id "main"} (h1 ($text "Hello, world!")) (p ($text "How do you do?")))
(div {:id "main"} (h1 ($text "Hello, world!")) (p ($text "How do you do?")))

ClojureScript:hlisp.env> (defn f [x] (div {:class "foo"} x))
#<function f(x){ ... }>

ClojureScript:hlisp.env> (f (p ($text "I am wrapped in a div?")))
(div {:class "foo"} (p ($text "I am wrapped in a div?")))
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


