(ns file-service.handler
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [file-service.routes.api :refer [app-routes]]))

(def app
  (-> app-routes
      (wrap-defaults (assoc-in site-defaults
                               [:responses :content-types]
                               false))
      (wrap-content-type {:mime-types {"json" "application/json"}})))
