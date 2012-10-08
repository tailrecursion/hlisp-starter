# HLisp Starter

HLisp is a lisp implementation built atop
[ClojureScript](https://github.com/clojure/clojurescript/wiki) that attempts to
mitigate several significant problems with interactive web development. A
prototype implementation demo can be viewed
[here](http://micha.github.com/hlisp-starter/).

## Motivation, Project Goals

This project aims to address some of the causes of incidental complexity in
frontend (user interface) web development.

* **Too many disparate domains.**

  The programmer must manage too many tightly coupled, fundamentally
  incompatible subsystems: HTML markup, CSS rules, JavaScript behaviors/events,
  HTML templating tools, etc. The HTML itself may be generated by server
  side code that the programmer must write in yet another programming language.

  These domains do not have a common set of primitives, means of abstraction,
  or means of composition. This results in having no coherent _meaning_
  for entities in these systems: entities do not have a single identity or
  way to manage references to them, they can't be composed with each other
  in a uniform way.

  The primary technique for managing complexity is the use of modular code and
  the separation of concerns. The tight coupling of disparate domains torpedoes
  this approach. Concerns which may be orthogonal in one domain may not be
  orthogonal in one of the other domains. The ability to create more specific
  abstractions by composition of more general ones is greatly restricted.

  **Goal:** Single, unified set of primitives, means of abstraction, and
  means of composition.
  
* **Pervasive use of global references and shared, mutable state.**

  The lack of a common set of primitives or means of abstraction/composition
  dictates the use of global identifiers to pass information between
  domains. This could be setting the `id` attribute of an element, or it
  could be a more complicated selector-based mechanism.

  **Goal:** Unified evaluation environment and namespace with lexical scope for
  all entities.

* **Callback based event handling.**

  The browser event handling system is designed around a callback
  mechanism. Callbacks generally have the void type (aside from those that
  return boolean false to cause side effects). Thus, there is no way to
  compose them, and they must rely entirely on side effects and shared state
  to perform their tasks.

  Event streams are not first-class entities, so there is no way to form
  abstractions around them. They can't be composed with each other. They can't
  be locally referenced. This forces even more shared state upton the system.

  **Goal:** Push-based event system, first-class event streams, and composable
  event handlers.

### Additional Requirements

* **Don't get rid of HTML.**

  Designers will need to work with HTML markup. The browser renders it. The
  elimination of HTML markup would necessarily introduce complexity in the
  form of an interface between the designer and the programmer, at the very
  least. So if possible, keep HTML markup as the primary means of building the
  front end DOM structure.

* **Simple to learn, minimal tooling to get started.**

  The system should be learnable by the motivated designer. They shouldn't have
  to be skilled programmers to use it. The system should expose useful
  functionality to the user without requiring a lot of boilerplate setup code.

* **Robust set of libraries.**

  It must be practical to build extensive libraries of modular code. This means
  some mechanism for managing references, namespaces, etc. Some way to
  distribute libraries and include them in projects is also key.

## HTML is Semantically Compatible with Lisp

To achieve the above goals, it is necessary to perform all front-end processing
in a single, unified evaluation environment. The semantic structure of HTML
suggests a [Lisp](http://en.wikipedia.org/wiki/Lisp_(programming_language) type
language, for the following reasons:

* **HTML structure is fundamentally lists.**

  A DOM element is semantically a list structure. Tools like
  [hiccup](https://github.com/weavejester/hiccup) use this characteristic to
  advantage.

* **DOM appendChild is semantically equivalent to Lisp function application.**

  With one caveat (elaborated below), appending children to a DOM element is
  semantically equivalent to Lisp function application. This is also the
  means of composition in both systems.

## A Possible Solution

Is Lisp an acceptable environment for the full user interface stack? Does Lisp
provide a solution to the above problems?

* **The HTML document can be evaluated as a Lisp program.**

  Given that HTML and Lisp are semantically compatible, it's possible to
  actually evaluate an HTML document as a Lisp program. The browser could be
  made to evaluate the body of the document when the page loads and replace
  the body contents with the result.

* **Lisp provides rich means of abstraction.**

  The abstraction-building capability of Lisp is well known.

* **Lisp is flexible enough to encompass all of the disparate domains.**

  The power and flexibility of a full-featured Lisp is more than capable of
  handling the demands of user interface programming. Templating tools,
  event handling, etc. can all be implemented in Lisp.

* **Evaluating HTML as Lisp unifies the evaluation environment.**

  Since the markup is code that is evaluated in some environment, lexical
  scope, namespaced references, and all the other Lisp mechanisms for managing
  references is available in both the library code and the markup itself. The
  markup elements become first-class entities.

## Issues

What are the important issues that must be solved?

* **Interpreted vs. compiled Lisp.**

  Interpreted Lisp has a number of advantages: runtime eval, complete control
  over evaluation and environment. But there are disadvantages: performance
  suffers, JavaScript interop is complicated. Compiled Lisp should perform
  better and have better interop characteristics.

  **Solution:** Go with compiled Lisp, since an excellent implementation,
  ClojureScript, already exists.

* **Semantic differences between Lisp and HTML.**

  HTML is sort of a [Lisp-1.5](http://en.wikipedia.org/w/index.php?title=Common_Lisp&oldid=402600249#The_function_namespace)
  The [car](http://en.wikipedia.org/wiki/CAR_and_CDR) of the list representation
  of an HTML element is the tag name. This must be a symbol and can't be another
  list. This is a departure from Lisp's uniform treatment of list elements,
  where the evaluator evals the car as well as the cdr. HTML semantics can't
  express `((f x) y z)`, for example.

  This is a result of the fact that there are no HTML primitives other than the
  Element node, which means that all elements are semantically lists. This
  makes it impossible to express `(f)` (meaning function application with no
  arguments), without giving up the capability to express `f` (function as a
  value).

  **Solution:** Give up ability to call functions without arguments from HTML
  markup. This can be accounted for by simply requiring that all functions that
  are exposed to the user take an argument, even if they discard it. This
  preserves the ability to reference functions as values, which is key for the
  formation of higher-order functions, and is central to Lisp. In a functional
  system, zero-argument functions are rare, as they can only produce side
  effects. Additionally, some kind of [funcall](http://www.lispworks.com/documentation/HyperSpec/Body/f_funcal.htm) or [call](https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/Function/call) mechanism can be used to accomodate the former case.

* **Syntactic differences between Lisp and HTML.**

  Clearly, HTML is vastly more verbose than Lisp is. It would be exceedingly
  tedious to write a lot of functional code in HTML. A mechanism must be
  available by which the programmer can program in Lisp or HTML, as required.
  HTML tags also have different syntactic requirements from Lisp symbols. There
  are some symbols which are not [valid HTML tags](http://stackoverflow.com/questions/7065693/is-the-at-sign-a-valid-html-xml-tag-character).

  **Solution:** The document `<head>` must contain a
  `<script type="text/hlisp">` element. Lisp source can be contained in this
  script element, to be evaluated in the environment of the page. Furthermore,
  it's possible to simply refrain from naming user-level functions and entities
  with names that are not compatible with both.

* **Code libraries and namespaces.**

  How will library code be managed?

  **Solution:** ClojureScript has excellent support for libraries, namespaces,
  import and export of references, etc.

* **Event handlers and dynamic interface.**

  How will the goals of first-class event streams and composable callbacks be
  satisfied? Is there existing library code that can be used in situ or with
  reasonable modification?

  **Solution:** The [flapjax](http://flapjax-lang.org/) library is an excellent
  implementation of FRP in the browser. It provides all the features required.

* **Cross-browser DOM API library.**

  Which cross-browser DOM API library should be used?

  **Solution:** Hlisp core will be built on cljs, which includes the
  [Google Closure](https://developers.google.com/closure/) DOM libraries.
  However, projects built on top of the core could use other more user-friendly
  libraries.

* **HTML templating tools.**

  How will templating and HTML processing work in HLisp?

  **Solution:** None, yet. However, access to macros in cljs suggests that
  tools could be developed that far exceed the capabilities of current
  templating systems without giving up the unified evaluation environment.

* **Application data, server-client interaction.**

  How does the front end get the data it needs from the server?

  **Solution:** Data moves to and from the server via an RPC mechanism. This
  must include content-negotiation, authentication, authorization, anti-CSRF
  protection, and exception handling that may span the server and client. This
  RPC layer is a natural point of demarcation between interface and business
  logic. User interface issues notwithstanding, a robust business API is vital
  to the success of any complex project. By leveraging this API layer via the
  RPC system, a nice, uniform structure is maintained&mdash;the user interface
  is merely another client. Tools such as [wigwam](https://github.com/micha/wigwam)
  already exist and can be used.

## Architecture

An hlisp project consists of the following components:

* **The hlisp compiler.** Implemented as a [leiningen](https://github.com/technomancy/leiningen)
  plugin.

* **ClojureScript libraries.** Existing libraries can be used without
  modification in most cases. The only issue is names that are not valid HTML
  tags, but the soultion is usually simply to alias those refs.

* **Clojure macros.** Macros used in ClojureScript must be written in Clojure,
  which has certain limiting consequences. Hopefully full cljs macro support is
  on the way, though.

* **HTML source files.** The actual HTML source files that make up the site.

* **External JavaScript libraries.** JavaScript libraries such as jQuery and
  Flapjax, for instance, which can be used in the ClojureScript environment.

## Compiler

The hlisp compiler scans for HTML source files. For each source file found it
does the following:

1. Extract the `<script type="text/hlisp">` tag from the document `<head>`.
2. Use the cljs reader to read the text contents as cljs forms.
3. Extract the namespace declaration (must be the first form).
4. Insert a cljs "prelude" in between the namespace declaration and the rest of
   the forms from step 2. This prelude has definitions for things that need to
   be provided by hlisp in the page namespace. When/if support for `:use`
   without `:only` is available in cljs the prelude can be removed.
5. The document `<body>` is parsed as a cljs form, wrapped in a function
   definition, and appended to the other forms. This defines the page's
   `hlispinit` function, which is marked as `^:export`, so that the Closure
   compiler won't munge its name.
6. The cljs forms are written to a source file that will be compiled by the
   cljs compiler.
7. Initialization JavaScript for the page is generated and inserted into the
   HTML output, which is written to a file.

Then, when all HTML source files have been processed, the cljs compiler is
invoked, producing a single JavaScript file, _main.js_.

## Related Projects

* [lein-hlisp](https://github.com/micha/lein-hlisp) The HLisp compiler.
* [Hlisp](https://github.com/micha/hlisp) Previous attempt at interpreted Lisp.
* [Declarify.js](https://github.com/micha/declarify.js) Exploration of FRP in the browser.
* [Golf](https://github.com/golf/golf) Initial attempts.

## Demo

Here is the example `index.html` file:

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


