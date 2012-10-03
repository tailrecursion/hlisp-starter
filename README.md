# Development

Install [Leiningen2](https://github.com/technomancy/leiningen)

Install dependencies:

    lein deps

Start a server to serve the app on port 4000:

    script/serve

In another tab, you can start watcher-based ClojureScript compilation:

    script/autobuild

Visit [http://localhost:4000/](http://localhost:4000/) to see the app.

## Project

This is the default `project.clj` file:

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

## Running a ClojureScript REPL

### Emacs

* `C-x d` to the `cljs-starter` directory
* `M-x set-variable`, and set the variable `inferior-lisp-program` to `script/cljsrepl`
* `M-x run-lisp` and you should see an inferior-lisp buffer running a ClojureScript REPL.  Then, visit [http://localhost:4000/](http://localhost:4000/).
