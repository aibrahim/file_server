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

;;helper
(defn get-breadcrumb-items [path root-node]
  (let [splitted-path (rest (clojure.string/split path "/"))
        count-path (count splitted-path)
        splitted-root  (rest (clojure.string/split root-node "/"))
        count-root (count splitted-root)]
    (mapv 
     (fn [s]
       (let [l (take s splitted-path)]
         {:label (last l) :path (str "/" (clojure.string/join "/" l))})) (range count-root (inc count-path)))))

(def breadcrumb-interceptor [(rf/path :breadcrumb)])

(rf/reg-event-db
 ::update-breadcrumb
 breadcrumb-interceptor
 (fn [breadcrumb [_ path root-node]]
   (assoc breadcrumb :items (get-breadcrumb-items path root-node))))

(rf/reg-event-db
 ::reset-breadcrumb
 breadcrumb-interceptor
 (fn [breadcrumb _]
   (reduce dissoc breadcrumb (keys breadcrumb))))

