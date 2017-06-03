(ns beer.routes.style
  (:require [compojure.core :refer :all]
            [buddy.auth :refer [authenticated?]]
            [selmer.parser :refer [render-file]]
            [buddy.auth :refer [authenticated?]]
            [beer.models.db :as db]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [struct.core :as st]
            [clojure.set :refer [rename-keys]]
            [ring.util.response :refer [redirect]]))

(def style-schema
  {:id [st/required st/number]
   :description [st/required st/string]})

(defn style-validation? [params]
  (st/valid? {:id (read-string (:id params))
              :description (:description params)} style-schema))

(defn authenticated-admin? [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn get-styles [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-styles)
    (db/search-styles text)))

(defn get-style [page-name params session]
  (render-file page-name {:title "Style"
                          :logged (:identity session)
                          :style (first (db/find-style (select-keys params [:id])))
                          :beers (db/find-beer (rename-keys params {:id :style}))}))

(defn get-style-page [{:keys [params session]}]
  (cond
    (not (authenticated? session))
    (redirect "/login")
    (authenticated-admin? session)
    (get-style "templates/style-admin.html" params session)
    :else
    (get-style "templates/style-user.html" params session)))

(defresource search-styles [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :authorized? (fn [_] (authenticated-admin? session))
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                    (condp = media-type
                      "text/html" (render-file "templates/style-search.html"
                                               {:title "Search styles"
                                                :logged (:identity session)
                                                :styles (get-styles nil)})
                      "application/json" (->(:text params)
                                            (get-styles)
                                            (json/write-str)))))

(defresource update-style [{:keys [params session]}]
  :allowed-methods [:put]
  :available-media-types ["application/json"]
  :malformed? (fn [_] (not (style-validation? params)))
  :handle-malformed "Please provide id and description"
  :exists? (fn [_] (not (empty? (db/find-style (select-keys params [:id])))))
  :can-put-to-missing? false
  :new? false
  :respond-with-entity? true
  :authorized? (fn [_] (authenticated-admin? session))
  :put! (db/update-style params)
  :handle-ok (json/write-str "Style successfully edited")
  :handle-not-implemented (fn [_] (str "There is no style with id " (:id params))))

(defroutes style-routes
  (GET "/styles" request (search-styles request))
  (GET "/style/:id" request (get-style-page request))
  (PUT "/style" request (update-style request)))
