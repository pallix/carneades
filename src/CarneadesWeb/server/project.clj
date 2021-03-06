;; Copyright (c) 2014 Fraunhofer Gesellschaft
;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(defproject carneades/carneades-web "1.0.0-SNAPSHOT"
  :description "The REST and Web parts of the Carneades system."
  :url "http://carneades.github.io"
  :dependencies [[cider/cider-nrepl "0.8.0-SNAPSHOT"]
                 [org.clojure/clojure "1.6.0"]
                 [lib-noir "0.5.6"]
                 [compojure "1.1.5"]
                 [ring-server "0.2.8"]
                 [ring-middleware-format "0.3.1"]
                 [ring/ring-json "0.3.1"]
                 [clabango "0.5"]
                 [ring-mock "0.1.5"]

                 [org.slf4j/slf4j-log4j12 "1.7.5"]
                 [com.taoensso/timbre "3.1.6"]

                 [com.postspectacular/rotor "0.1.0"]
                 [com.taoensso/tower "1.5.1"]
                 [markdown-clj "0.9.28"]
                 [org.clojure/data.json "0.2.2"]

                 [carneades/carneades-engine "2.0.3"]

                 [clj-http "0.7.2"]
                 [clj-json "0.5.3"]
                 [cheshire "5.2.0"]
                 [http-kit "2.0.0"]

                 [liberator "0.11.0"]
                 [sandbar/sandbar "0.4.0-SNAPSHOT"]

                 [dire "0.4.3"] ;; uses logging 2.6.3

                 [midje "1.5.1"]]

  :plugins [[lein-ring "0.8.10"]
            [lein-midje "3.0.0"]
            [lein-exec "0.3.4"]]

  :ring {:handler carneades.web.handler/war-handler
         :init carneades.web.handler/init
         :destroy carneades.web.handler/destroy
         :servlet-name "carneades"
         :web-xml "web.xml"}

  :resource-paths ["../client/dist"]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [org.clojure/java.classpath "0.2.0"]]}

             :deploy {:ring {:handler carneades.web.handler/tomcat-war-handler
                             :init carneades.web.handler/init
                             :destroy carneades.web.handler/destroy
                             :servlet-name "carneades"}}

             :jar {:ring {:handler carneades.web.handler/jar-handler
                          :init carneades.web.handler/init-jar
                          :destroy carneades.web.handler/destroy
                          :servlet-name "carneades"}}}

  :repl-options {:timeout 120000}

  :min-lein-version "2.0.0")
