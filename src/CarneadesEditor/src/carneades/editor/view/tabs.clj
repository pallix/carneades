;;; Copyright © 2010 Fraunhofer Gesellschaft 
;;; Licensed under the EUPL V.1.1

(ns carneades.editor.view.tabs
  (:use clojure.contrib.def
        clojure.contrib.swing-utils)
  (:import (java.awt EventQueue event.MouseListener Dimension FlowLayout)
           (javax.swing UIManager JTabbedPane JLabel JButton JFrame JPanel
                        ImageIcon
                        JInternalFrame
                        JFileChooser
                        filechooser.FileFilter
                        SwingUtilities
                        BorderFactory
                        tree.DefaultMutableTreeNode
                        tree.TreeSelectionModel)
           (javax.swing.event ChangeListener)
           (carneades.editor.uicomponents EditorApplicationView)))

(defvar- *viewinstance* (EditorApplicationView/instance))

(defvar *mapPanel* (.mapPanel *viewinstance*))

(defvar- *closebutton-url* "carneades/editor/view/close-button.png")
(defvar- *closebutton-rollover-url*
  "carneades/editor/view/close-button-rollover.png")

(defvar- *close-button-listeners* (atom ()))

(defvar- *swingcomponents-to-ags* (ref {}) "components -> [path graphid]")
(defvar- *ags-to-components* (ref {}) "[path id] -> component")
(defvar- *component-to-title* (ref {}))

(defn get-graphinfo [component]
  (get (deref *swingcomponents-to-ags*) component))

(defn init-tabs []
  (.setTabLayoutPolicy *mapPanel* JTabbedPane/SCROLL_TAB_LAYOUT))

(defn register-close-button-listener [l args]
  (swap! *close-button-listeners* conj {:listener l :args args}))

(defn graphinfo-being-closed [event]
  "returns [path id]"
  (let [button (.getSource event)
        tabcomponent (.getParent button)
        idx (.indexOfTabComponent *mapPanel* tabcomponent)
        component (.getComponentAt *mapPanel* idx)]
    (get-graphinfo component)))

(defn create-close-button []
     (let [closebutton (JButton.)]
       (doto closebutton
         (.setBorder nil)
         (.setIcon (ImageIcon. (ClassLoader/getSystemResource
                                *closebutton-url*)))
         (.setRolloverIcon (ImageIcon. (ClassLoader/getSystemResource
                     *closebutton-rollover-url*)))
         (.setContentAreaFilled false)
         (.setRolloverEnabled true))
       (doseq [{:keys [listener args]} (deref *close-button-listeners*)]
         (apply add-action-listener closebutton listener args))
       closebutton))

(defn create-tabcomponent [title]
  (let [tabcomponent (JPanel.)
        label (JLabel. title)
        closebutton (create-close-button)]
    (.setOpaque tabcomponent false)
    (.setFocusable label false)
    (.setFocusable tabcomponent false)
    (.setBorder tabcomponent (BorderFactory/createEmptyBorder 0 0 0 0))
    (.setLayout tabcomponent (FlowLayout. FlowLayout/LEFT 0 0))
    (.add tabcomponent label)
    (.add tabcomponent closebutton)
    {:tabcomponent tabcomponent :label label}))

(defn get-tabtitle [ag isdirty]
  (if (empty? (:title ag))
    (str (if isdirty "*") (format "%s [title missing]" (:id ag)))
    (str (if isdirty "*") (:title ag))))

(defn add-component [graphcomponent path ag isdirty]
  (let [title (get-tabtitle ag isdirty)
        id (:id ag)
        component (:component graphcomponent)
        tab (create-tabcomponent title)]
    (.add *mapPanel* title component)
    (.setTabComponentAt *mapPanel*
                        (.indexOfComponent *mapPanel* component)
                        (:tabcomponent tab))
    (.setSelectedComponent *mapPanel* component)
    (dosync
     (alter *swingcomponents-to-ags* assoc component [path id])
     (alter *ags-to-components* assoc [path id] graphcomponent)
     (alter *component-to-title* assoc component (:label tab)))))

(defn get-component [path id]
  (get (deref *ags-to-components*) [path id]))

(defn remove-component [graphcomponent]
  (let [component (:component graphcomponent)
        info (get-graphinfo component)]
    (.remove *mapPanel* component)
    (dosync
     (alter *swingcomponents-to-ags* dissoc component)
     (alter *component-to-title* dissoc component)
     (alter *ags-to-components* dissoc info))))

(defn tabs-empty? []
  "true if no tabs"
  (empty? (deref *swingcomponents-to-ags*)))

(defn select-component [component]
  (.setSelectedIndex *mapPanel*
                     (.indexOfComponent *mapPanel* (:component component))))

(defn set-tab-dirty [path id isdirty]
  (if-let [component (:component (get (deref *ags-to-components*) [path id]))]
    (if-let [label (get (deref *component-to-title*) component)]
      (let [oldtext (.getText label)]
        (cond (and isdirty (not= (first oldtext) \*))
              (.setText label (str "*" oldtext))

              (not isdirty)
              (do
                (prn "unsetting dirty")
                (.setText label (.substring oldtext 1)))))
      (do
        (prn "title not found")))
    (do
      (prn "component not found"))))

(defn register-tab-change-listener [listener]
  (.addChangeListener *mapPanel* listener))
