(ns beer.routes.bs
  (:require [compojure.core :refer :all]
            [buddy.auth :refer [authenticated?]]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [ring.util.response :refer [response redirect content-type]])
  (:import [beer.models.question Question]))

(defn authenticated [session]
  (authenticated? session))

(defn get-bs [text]
  (if (or (nil? text) (= "" text))
    (db/get-beer-styles)
    (db/search-beer-styles (str "%" text "%"))))

(defn update-bs [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/beer-edit.html" {:title "Edit beer style"
                                             :message (str "Beer style successfully edited, id: " (db/update-beer-style params))})))

(defn get-search-bs [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/bs-search.html" {:title "Search beer styles" :logged (:identity session) :beers (get-bs nil)})))

(defresource search-bs [{:keys [params session] request :request}]
  :allowed-methods [:post]
  :authenticated? (authenticated session)
  :handle-created (json/write-str (get-bs (:text params)))
  :available-media-types ["application/json"])

(defn find-bs [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/bs.html" {:title "Beer Style" :logged (:identity session)
                                        :bs (first (db/find-beer-style-by-id (:id params)))
                                    :beers (db/get-beers-for-beer-style (:id params ))})))

(defroutes bs-routes
  (GET "/bs" request (get-search-bs request))
  (POST "/bs" request (search-bs request))
  (GET "/bs/:id" request (find-bs request))
  (PUT "/bs/:id" request (update-bs request))
  )
