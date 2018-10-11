(ns file-service.events
  (:require
   [re-frame.core :as rf]
   [file-service.db :as db]
   [ajax.core :refer [GET]]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 ::set-root-node
 (fn [db [_ node]]
   (assoc db :root-node node)))

(rf/reg-event-db
 ::read-path
 (fn [db [_ path root?]]
   (GET (str "/api/v1/fs/read?path=" path)
        {:handler #(rf/dispatch [::read-path-success %])
         :response-format :json
         :keywords? true
         ::error-handler #(rf/dispatch [::read-path-fail %])})
   db))

(rf/reg-event-db
 ::read-path-success
 (fn [db [_ {:keys [body]}]]
   (if (:content body)
     (assoc db :file-content body)
     (assoc db :dir-tree body))))

(rf/reg-event-db
 ::read-path-error
 (fn [db [_ response]]
   (.log js/console response)))

(def breadcrumb-interceptor [(rf/path :breadcrumb)])

(rf/reg-event-db
 ::add-breadcrumb
 breadcrumb-interceptor
 (fn [breadcrumb [_ item]]
   (let [item-name (-> item (clojure.string/split "/") last)]
     (assoc breadcrumb item {:label item-name}))))

(rf/reg-event-db
 ::reset-breadcrumb
 breadcrumb-interceptor
 (fn [breadcrumb _]
   (reduce dissoc breadcrumb (keys breadcrumb))))

