;;; Copyright © 2010 Fraunhofer Gesellschaft 
;;; Licensed under the EUPL V.1.1

(ns carneades.editor.view.dialogs.location
  (:use clojure.contrib.swing-utils
        carneades.editor.view.viewprotocol
        [carneades.editor.view.components.uicomponents :only (*frame*)])
  (:import (carneades.editor.uicomponents LocationDialog)))

(defn show-location-dialog [view]
  (let  [dialog (LocationDialog. *frame* true)
         okbutton (.okbutton dialog)
         cancelbutton (.cancelbutton dialog)
         locationtext (.locationText dialog)
         browsebutton (.browseButton dialog)
         relativebutton (.relativePathsButton dialog)
         location (atom nil)]
    (add-action-listener relativebutton
                         (fn [event]
                           (when-let [location (deref location)]
                             (swap! location assoc
                                    :relative (.getSelected relativebutton)))))
    (add-action-listener cancelbutton
                         (fn [event]
                           (.dispose dialog)
                           (reset! location nil)))
    (add-action-listener okbutton
                         (fn [event]
                           (.dispose dialog)
                           (reset! location {:location (.getText locationtext)
                                             :relative (.isSelected relativebutton)})))
    (add-action-listener browsebutton
                         (fn [event]
                           (when-let [path
                                      (ask-file-to-open view
                                                        "LKIF or OWL files"
                                                        #{"xml" "lkif" "owl"})]
                             (let [path (.getPath path)]
                               (.setText locationtext path)
                               (reset! location {:location path
                                                 :relative (.isSelected relativebutton)})))))
    (.setLocationRelativeTo dialog *frame*)
    (.setVisible dialog true)
    (deref location)))
