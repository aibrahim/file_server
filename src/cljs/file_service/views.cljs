(ns file-service.views
  (:require
   [antizer.reagent :as ant]
   [re-frame.core :as rf]
   [reagent.core :as reagent]
   [file-service.subs :as subs]
   [file-service.events :as events]))

(.log js/console "Welcome to File service!!")

(def default-files-color "#cdc486")

(def icon-types
  {"pdf" {:icon "file-pdf" :color "red"}
   "docx" {:icon "file-word" :color "blue"}
   "xls" {:icon "file-excel" :color "green"}
   "xlsx" {:icon "file-excel" :color "green"}
   "jpg" {:icon "file-jpg" :color "orange"}
   "ppt" {:icon "file-ppt" :color "black"}
   "md" {:icon "file-markdown" :color "purple"}
   "txt" {:icon "file-text" :color "brown"}
   nil {:icon "file-unknown" :color "grey"}})

;add whatever you would like
(def text-formats ["txt" "md" "csv" "clj" "cljs" "cpp" "py" "hs" "rb" "js" "erl" "rc" "xml" "json" "go" "c" "r" "html"])

(defn header []
  (let [name @(rf/subscribe [::subs/name])]
    [ant/layout-header
     [:h1 {:style {:color "white"}}
      name]]))

(defn root-node-input []
  (let [root-node @(rf/subscribe [::subs/root-node])]
    [ant/card
     [ant/row {:gutter 24}
      [ant/col {:offset 6 :span 8}
       [ant/input {:placeholder "Enter root node path please"
                   :value root-node
                   :on-change #(rf/dispatch [::events/set-root-node (.. % -target -value)])}]]
      [ant/col
       [ant/button {:on-click #(do
                                 (rf/dispatch [::events/read-path root-node])
                                 (rf/dispatch [::events/reset-breadcrumb])
                                 (rf/dispatch [::events/update-breadcrumb root-node root-node]))} "start"]]]]))

(defn breadcrumb-ui [{:keys [items]}]
  [ant/breadcrumb {:separator ">"}
   (doall
    (for [{:keys [label path]} items]
      [ant/breadcrumb-item {:href "#" :key path :on-click #(rf/dispatch [::events/read-path path])} label]))])

(defn get-extension [p]
  (let [ext (clojure.string/split p ".")]
    (if (> (count ext) 1)
      (-> ext last clojure.string/lower-case))))

(defn find-icon-type [type ext]
  (if (= type "file")
    (if-let [{:keys [icon color]} (get icon-types ext)]
      {:icon icon :color color}
      {:icon type})
    {:icon type}))

(defn files-ui [files]
  (let [root-node @(rf/subscribe [::subs/root-node])]
    [ant/row
     (doall
      (for [{:keys [path file-name type]} files]
        (let [ext (get-extension file-name)
              {:keys [icon color]} (find-icon-type type ext)]
          [ant/col {:key path :span 8}
           [:div {:on-click #(condp = type
                               "folder" (do 
                                          (rf/dispatch [::events/read-path path])
                                          (rf/dispatch [::events/update-breadcrumb path root-node]))
                               "file" (cond
                                        (some (fn [e] (= e ext)) text-formats) (rf/dispatch [::events/read-path path])
                                        :else (.log js/console "Not supported!")
                                      ;;;;;; you can add more here ;;;;;;;
                                        ))}
            [ant/icon {:type icon :style {:font-size "25px" :color (or color default-files-color)}}]
            [:h5 file-name]]])))]))

(defn dir-hierarchy []
  (let [{:keys [childrens] :as tree} @(rf/subscribe [::subs/dir-tree])
        breadcrumb-items @(rf/subscribe [::subs/breadcrumb])]
    [ant/col {:span 12}
     [ant/card
      [:h3 "File manager"]
      [ant/row
       [ant/col
        [breadcrumb-ui breadcrumb-items]] [:br]
       [files-ui childrens]]]]))

(defn file-view []
  (let [{:keys [path file-name content]} @(rf/subscribe [::subs/file-content])]
    [ant/col {:span 12}
     [ant/card
      [:h3 path] [:hr]
      [:div
       [:pre content]]]]))

(defn results-ui []
  (let [dir-name @(rf/subscribe [::subs/root-node])]
    [:div {:class "results"}
     [dir-hierarchy]
     [file-view]]))

(defn content []
  [:div {:class "content"}
   [root-node-input] [:br]
   [results-ui]])

(defn footer []
  (let [footer @(rf/subscribe [::subs/footer])]
    [ant/layout-footer
     [:h3 footer]]))

(defn main-panel []
  (fn []
    [ant/locale-provider {:locale (ant/locales "en_US")}
     [ant/layout
      [header]
      [content]
      [footer]]]))
