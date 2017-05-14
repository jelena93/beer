(ns beer.routes.bs
  (:require [compojure.core :refer :all]
            [buddy.auth :refer [authenticated?]]
            [selmer.parser :refer [render-file]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [ring.util.response :refer [response redirect content-type]])
  (:import [beer.models.question Question]))

(defn authenticated [session]
  (authenticated? session))

(defn authenticated-admin [session]
  (if (and (not (authenticated? session))
       (not="admin" (:role (:identity session))))
    (throw-unauthorized {:message "Not authorized"})))

(defn check-authenticated-admin [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn get-bs [text]
  (if (or (nil? text) (= "" text))
    (db/get-beer-styles)
    (db/search-beer-styles (str "%" text "%"))))

(defn get-search-bs [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
  (render-file "templates/bs-search.html" {:title "Search beer styles"
                                           :logged (:identity session) :bs (get-bs nil)})))

(defn find-bs [{:keys [params session] request :request}]
  (println (not (authenticated session)))
  (cond
    (not (authenticated session))
     (redirect "/login")
    (check-authenticated-admin session)
     (render-file "templates/bs-admin.html" {:title "Beer Style" :logged (:identity session)
                                        :bs (first (db/find-beer-style-by-id (:id params)))
                                    :beers (db/get-beers-for-beer-style (:id params ))})
    :else
     (render-file "templates/bs-user.html" {:title "Beer Style" :logged (:identity session)
                                        :bs (first (db/find-beer-style-by-id (:id params)))
                                    :beers (db/get-beers-for-beer-style (:id params ))})))

(defresource search-bs [{:keys [params session] request :request}]
  :allowed-methods [:post]
  :authenticated? (not (check-authenticated-admin session))
  :handle-created (json/write-str (get-bs (:text params)))
  :available-media-types ["application/json"])

(defresource update-bs [{:keys [params session] request :request}]
  :allowed-methods [:put]
  :authenticated? (not (check-authenticated-admin session))
  :put! (db/update-beer-style params)
  :handle-created (json/write-str "Beer style successfully edited")
  :available-media-types ["application/json"])

(defroutes bs-routes
  (GET "/bs" request (get-search-bs request))
  (POST "/bs" request (search-bs request))
  (GET "/bs/:id" request (find-bs request))
  (PUT "/bs/:id" request (update-bs request)))
