(ns file-service.routes.api
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response]
            [cheshire.core :refer [generate-string]]
            [file-service.models.fs :refer [read-path]]))

(defn wrap-json [body]
  (-> body
      response/response
      (response/content-type "application/json")
      generate-string))

(defroutes app-routes
  (GET "/" [] (response/redirect "/index.html"))
  (GET "/api/v1/fs/read" [path]
       (let [results (read-path path)
             _ (prn results)]
         (wrap-json results)))
  (route/not-found "Not Found"))



