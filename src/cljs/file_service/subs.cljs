(ns file-service.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

(rf/reg-sub
 ::footer
 (fn [db]
   (:footer db)))

(rf/reg-sub
 ::root-node
 (fn [db]
   (:root-node db)))

(rf/reg-sub
 ::dir-tree
 (fn [db]
   (:dir-tree db)))

(rf/reg-sub
 ::breadcrumb
 (fn [db]
   (:breadcrumb db)))

(rf/reg-sub
 ::file-content
 (fn [db]
   (:file-content db)))
