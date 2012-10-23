(ns catb.icanhaz.core
  (:use [jayq.util :only [log clj->js]])
  (:require [clojure.string :as str])
  (:refer-clojure :exclude [get]))

(defn get
  "Returns the ICanHaz HTML template"
  [template-key variables]
  (let [tname (str/replace (clj->js template-key) "-" "_")]
    (log "icanhaz log")
    (log (clj->js variables))
    (.call (aget js/ich tname) js/ich (clj->js variables))))