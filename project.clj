(defproject
  lein-test     "0.1.0-SNAPSHOT"
  :description  "FIXME: write description"
  :url          "http://example.com/FIXME"
  :license      {:name "Eclipse Public License"
                 :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :plugins      [[lein-hlisp "0.1.0-SNAPSHOT"]
                 [lein-ring "0.7.1"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.0.4"]]
  :eval-in      :leiningen
  :ring         {:handler example.routes/app}
  :hlisp        {:html-src    "src/html"
                 :cljs-src    "src/cljs"
                 :html-out    "resources/public"
                 :includes    ["src/jslib/jquery.js"]
                 :cljsc-opts  {:optimizations  :whitespace
                               :externs        ["src/extern/jquery.js"]}} 

  )
