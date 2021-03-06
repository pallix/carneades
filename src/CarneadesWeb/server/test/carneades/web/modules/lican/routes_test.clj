(ns carneades.web.modules.lican.routes-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :refer :all]
            [taoensso.timbre :as timbre :refer [debug info spy]]
            [carneades.web.handler :refer [app]]
            [cheshire.core :refer [parse-string encode]]
            [carneades.engine.utils :as utils]))

(def base-url "/carneades/api")

(defn parse
  [s]
  (parse-string s true))

(defn post-request
  [url content]
  (app (-> (request :post
                    (str base-url url))
           (body (encode content))
           (content-type "application/json"))))

;; (fact "It is possible to use a GPL library from a GPL software."
;;       (let [content {"legalprofile" ""
;;                      "licensetemplateuri" "http://www.markosproject.eu/ontologies/oss-licenses#GPL-2.0"
;;                      "usepropertyuris" ["http://www.markosproject.eu/ontologies/software#dynamicallyLinkedEntity"]
;;                      "swentityuris" ["http://markosproject.eu/kb/Library/549"]
;;                      }
;;             res (post-request "/lican/findsoftwareentitieswithcompatiblelicenses" content)
;;             body-content (parse (:body res))]
;;         body-content => '("http://markosproject.eu/kb/Library/549")))

(fact "It is not possible to use a GPL library from an Apache software."
      (let [content {"legalprofile" "1"
                     "licensetemplateuri" "http://www.markosproject.eu/ontologies/oss-licenses#Apache-2.0"
                     "usepropertyuris" ["http://www.markosproject.eu/ontologies/software#dynamicallyLinkedEntity"]
                     "swentityuris" ["http://markosproject.eu/kb/SoftwareRelease/1948" ;; LGPL
                                     "http://markosproject.eu/kb/SoftwareRelease/1955" ;; GPL
                                     "http://markosproject.eu/kb/SoftwareRelease/1960" ;; Apache
                                     "http://markosproject.eu/kb/JavaLibrary/1068" ;; Apache 1.1
                                     
                                     ]
                     }
            res (post-request "/lican/findsoftwareentitieswithcompatiblelicenses" content)
            body-content (parse (:body res))]
        body-content => '("http://markosproject.eu/kb/SoftwareRelease/1960"
                          "http://markosproject.eu/kb/JavaLibrary/1068")))

(fact "The onlinetour is compatible with the GPL 3.0"
      (let [content {"legalprofile" "1"
                     "softwareentity" "http://markosproject.eu/kb/SoftwareRelease/3770"
                     }
            res (post-request "/lican/findcompatiblelicenses" content)
            body-content (parse (:body res))]
        body-content => '("http://www.markosproject.eu/ontologies/oss-licenses#GPL-3.0")))
