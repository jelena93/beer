(ns beer.routes.style
  (:require [compojure.core :refer :all]
            [buddy.auth :refer [authenticated?]]
            [selmer.parser :refer [render-file]]
            [buddy.auth :refer [authenticated?]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [ring.util.response :refer [response redirect content-type]])
  (:import [beer.models.question Question]))

(defn authenticated-admin? [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn get-styles [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-styles)
    (db/search-styles (str "%" text "%"))))

(defn get-style-page [{:keys [params session]}]
  (cond
    (not (authenticated? session))
     (redirect "/login")
    (authenticated-admin? session)
     (render-file "templates/style-admin.html" {:title "Style"
                                             :logged (:identity session)
                                             :style (first (db/find-style-by-id (:id params)))
                                             :beers (db/get-beers-for-style (:id params))})
    :else
     (render-file "templates/style-user.html" {:title "Style"
                                            :logged (:identity session)
                                            :style (first (db/find-style-by-id (:id params)))
                                            :beers (db/get-beers-for-style (:id params))})))

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
                      "application/json" (json/write-str (get-styles (:text params))))))

(defresource update-style [{:keys [params session]}]
  :allowed-methods [:put]
  :available-media-types ["application/json"]
  :malformed? (fn [context] (and (empty? (:id params))
                                 (empty? (:description params))))
  :handle-malformed "Please provide a new password"
  :new? false
  :respond-with-entity? true
  :authorized? (fn [_] (authenticated-admin? session))
  :put! (db/update-style params)
  :handle-ok (json/write-str "Style successfully edited"))

(defroutes style-routes
  (GET "/styles" request (search-styles request))
  (GET "/style/:id" request (get-style-page request))
  (PUT "/style/:id" request (update-style request)))
