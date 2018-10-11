(ns file-service.models.fs
  (:require [clojure.java.io :as io]))

(defn fname [p]
  (-> p
      io/file
      (.getName)))

(defn wrap-results [t p content]
  (let [file-name (fname p)
        h {:type t :path p :file-name file-name}]
    (if (= t :folder)
      (assoc h :childrens content)
      (assoc h :content content))))

(defn get-file-type [p]
  (let [dir? (.isDirectory (clojure.java.io/file p))]
    (if dir? 
      :folder
      :file)))

(defn ls [dir]
  (->> dir
       io/file
       (.listFiles)
       seq
       (mapv 
        (fn [f] 
          (let [path (.getPath f)
                name (.getName f)
                type (get-file-type path)]
            {:path path :type type :file-name name})))))

;permissions are needed to be handled.
(defn read-path [p]
  (let [type (get-file-type p)
        reader (condp = type
                 :file slurp ;should be modified here, not every file should be slurped for example: pdf, excel file..
                 :folder ls
                 (throw (Exception. {:error "not supported"})))]
    (try
      (->> p
           reader
           (wrap-results type p))
      (catch java.io.FileNotFoundException e {:error (.getMessage e)}))))
