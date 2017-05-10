(ns beer.routes.beer
  (:require [compojure.core :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated?]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [ring.util.response :refer [response redirect content-type]]))

(defn authenticated [session]
  (authenticated? session))

(defn get-add-beer [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/beer-add.html" {:title "Add beer" :logged (:identity session) })))

(defn add-beer [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/beer-add.html" {:title "Add beer" :logged (:identity session) :message (str "Beer successfully added, id: " (db/add-beer params))})))

(defn update-beer [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/beer-edit.html" {:title "Edit beer" :logged (:identity session) :message (str "Beer successfully edited, id: " (db/update-beer params))})))

(defn get-beers [text]
  (if (or (nil? text) (= "" text))
    (db/get-beers)
    (db/search-beers (str "%" text "%"))))

(defn get-search-beers [{:keys [params session] request :request}]
  (render-file "templates/beer-search.html" {:title "Search users" :logged (:identity session) :beers (get-beers nil)}))

(defresource search-beers [{:keys [params session] request :request}]
  :allowed-methods [:post]
  :authenticated? (authenticated session)
  :handle-created (json/write-str (get-beers (:text session)))
  :available-media-types ["application/json"])

(defresource delete-beer [{:keys [params session] request :request}]
  :allowed-methods [:delete]
  :handle-malformed "beer id cannot be empty"
  :authenticated? (authenticated session)
  :delete! (db/delete-beer (:id params))
  :handle-created (json/write-str "ok")
  :available-media-types ["application/json"])

(defn find-beer [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/beer.html" {:title "Beer" :logged (:identity session) :beer (first (db/find-beer-by-id (:id params)))})))

(defn admin-view []
  (response "ADMINS ONLY"))

(defn admin [{session :session}]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defroutes beer-routes
  (GET "/beer" request (get-add-beer request))
  (POST "/beer" request (add-beer request))
  (PUT "/beer" request (update-beer request))
  (DELETE "/beer" request (delete-beer request))
  (GET "/beers" request (get-search-beers request))
  (POST "/beers" request (search-beers request))
  (GET "/beer/:id" request (find-beer request))
;;   (GET "/beer" [] (restrict admin-view {:handler admin}))
  )

;; {:keys [params] session :session}
;; (defn is-admin [{user :identity :as session}]
;; (defn is-admin [{{:keys [identity] :session}}]
;;   (println "usao" session)
;;   (println "ss" identity)
;;   (contains? (apply hash-set (:roles identity)) "admin"))
