;;; Copyright (c) 2012 Fraunhofer Gesellschaft
;;; Licensed under the EUPL V.1.1

(defproject carneades-webapp/carneades-webapp "1.0.0-SNAPSHOT"
  :description "The Carneades Web Application"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.1.1"]
                 [compojure "1.0.4" :exclusion [clojure]]
                 [hiccup "0.3.6"]
                 [enlive "1.0.0"]
                 [ring/ring-servlet "1.0.1"]
                 [ring-middleware-format "0.2.3"]
                 [carneades-engine "2.0.0-SNAPSHOT"]
                 [carneades-web-service "1.0.0-SNAPSHOT"]
                 [org.clojars.pallix/mygengo "1.0.0"]
                 ;; [jayq "0.1.0-alpha4"]
                 [cc.qbits/jayq "0.1.0-alpha4-SNAPSHOT"]
                 [lein-ring "0.5.4"]
                 [org.clojure/tools.logging "0.2.3"]]
  :plugins [[lein-ring "0.7.1"]
            [lein-cljsbuild "0.3.0"]
            [lein-sub "0.2.3"]]
  ;; sub projects
  :sub ["../CarneadesEngine" "../CarneadesWebService" "../CarneadesExamples"]
  ; Enable the lein hooks for: clean, compile, test, and jar.
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds
              [{:id "dev",
                :source-paths ["src-cljs" "src-js"],
                :compiler
                {:pretty-print true,
                 :libs ["libs"]
                 :output-to "resources/carneades/public/js/compiled-app.js",
                 :optimizations :whitespace},
                :jar true}
               {:id "test",
                :source-paths ["test-cljs"],
                :compiler
                {:pretty-print true,
                 :output-to "resources/carneades/private/js/unit-test.js",
                 :optimizations :whitespace}}
               {:id "prod",
                :source-paths ["src-cljs" "src-js"]
                :compiler
                {:pretty-print false,
                 :output-to "resources/carneades/public/js/compiled-app.js",
                 :libs ["libs"]
                 :optimizations :simple}}],
              :test-commands
              {"questions"
               ["casperjs"
                "--dir=resources/carneades/private/html/"
                "--url=test-questions.html"
                "casper/run-unit-test.js"]},
              :repl-listen-port 9000,
              :repl-launch-commands
              {"firefox-naked"
               ["firefox"
                "resources/private/html/naked.html"
                :stdout
                ".repl-firefox-naked-out"
                :stderr
                ".repl-firefox-naked-err"],
               "phantom"
               ["phantomjs"
                "phantom/repl.js"
                :stdout
                ".repl-phantom-out"
                :stderr
                ".repl-phantom-err"],
               "phantom-naked"
               ["phantomjs"
                "phantom/repl.js"
                "resources/private/html/naked.html"
                :stdout
                ".repl-phantom-naked-out"
                :stderr
                ".repl-phantom-naked-err"],
               "firefox"
               ["firefox" :stdout ".repl-firefox-out" :stderr ".repl-firefox-err"]}}
  :ring {:handler carneades.web.routes-dev/carneades-webapp}
  :profiles {:standalone {:main carneades.web.routes-selfexe}
             :war {:ring {:handler carneades.web.routes-war/impact-app}
                   :sub ["../CarneadesWebService"]}}
  )

