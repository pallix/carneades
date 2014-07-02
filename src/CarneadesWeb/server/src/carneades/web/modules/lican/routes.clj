;; Copyright (c) 2014 Fraunhofer Gesellschaft
;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns carneades.web.modules.lican.routes
  ^{:doc "Definition of project routes."}
  (:require [sandbar.stateful-session :refer :all]
            [ring.middleware.session.cookie :refer :all]
            [ring.middleware.json :refer [wrap-json-response]]
            [compojure.core :refer [defroutes context ANY GET POST PUT]]
            [liberator.core :refer [defresource]]
            [clojure.set :as set]
            [clabango.parser :as parser]
            [noir.request :refer :all]
            [carneades.engine.utils :refer [unserialize-atom]]
            [carneades.project.admin :as project]
            [carneades.web.modules.lican.entity :as entity]
            [carneades.web.modules.lican.analysis :as analysis]
            [carneades.web.modules.lican.dbg-analysis :as debug-analysis]
            [taoensso.timbre :as timbre :refer [debug info warn error]]))

(defresource entry-entity-resource [pid uri]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :available-charsets ["utf-8"]
  :handle-ok (fn [_] (entity/get-software-entity pid (unserialize-atom uri))))

(defresource answers-send-resource [answs uuid]
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  :available-charsets ["utf-8"]
  :handle-created (fn [ctx]
                    (::questions ctx))
  :post! (fn [ctx] (assoc ctx ::questions (analysis/process-answers answs uuid))))

(defresource entry-analyse-resource [entity legalprofile]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :available-charsets ["utf-8"]
  :handle-ok (fn [_]
               (debug "legalprofile " legalprofile)
               (analysis/analyse entity legalprofile)))

(defresource entry-dbg-analyse-resource [e r q l]
  ;; q := query; l := limit; e := endpoint; r:= repo-name
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  :available-charsets ["utf-8"]
  :post! (fn [ctx] {::body (debug-analysis/analyse e r q l)}))

(defresource entry-dbg-query-resource [e r q l]
  ;; q := query; l := limit; e := endpoint; r:= repo-name
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  :available-charsets ["utf-8"]
  :post! (fn [ctx] {::body (debug-analysis/query e r q l)}))

(defresource entry-dbg-ask-resource [e r q l]
  ;; q := query; l := limit; e := endpoint; r:= repo-name
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  :available-charsets ["utf-8"]
  :post! (fn [ctx] {::body (debug-analysis/ask e r q l)}))

(defroutes carneades-lican-api-routes
  (GET "/analyse" [entity legalprofile] (entry-analyse-resource entity legalprofile))

  (context "/entities" []
           (ANY "/:pid" [pid uri] (entry-entity-resource pid uri)))

  (context "/answers" []
           (ANY "/send" {{answers :answers uuid :uuid} :body-params}
                (answers-send-resource answers uuid)))

  (context "/debug" [q l e r]
           ;; q := query; l := limit; e := endpoint; r:= repo-name
           (ANY "/analyse" [] (entry-dbg-analyse-resource e r q l))
           (ANY "/query" [] (entry-dbg-query-resource e r q l)) (ANY "/ask" [] (entry-dbg-ask-resource e r q l))))
