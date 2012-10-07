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
                 :html-out    "."
                 :base-dir    "/hlisp-starter"
                 :includes    ["src/jslib/jquery.js"
                               "src/jslib/flapjax.js"]
                 :cljsc-opts  {:optimizations  :whitespace
                               :externs        ["src/extern/jquery.js"
                                                "src/extern/flapjax.js"]}})
