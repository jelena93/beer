(ns beer.routes.user
  (:require [compojure.core :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated?]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [ring.util.response :refer [response redirect content-type]]))

(defn get-users [text]
  (if (or (nil? text) (= "" text))
    (db/get-users)
    (db/search-users (str "%" text "%"))))

(defn get-search-users [{:keys [params session] request :request}]
  (println (db/get-users))
  (render-file "templates/user-search.html" {:title "Search users" :logged (:identity session) :users (get-users nil)}))

(defn add-user [{session :session}]
  (render-file "templates/add-user.html" {:title "Add user" :logged (:identity session)}))

(defn get-user [{:keys [params session] request :request}]
  (render-file "templates/user.html" {:title (str "User " (:id params)) :logged (:identity session) :user (db/get-user (:id params))}))

(defn admin-view []
  (response "ADMINS ONLY"))
;; {:keys [params] session :session}
;; (defn is-admin [{user :identity :as session}]
;; (defn is-admin [{{:keys [identity] :session}}]
;;   (contains? (apply hash-set (:roles identity)) "admin"))

(defn admin [{session :session}]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defresource search-users [text]
  :allowed-methods [:post]
  :handle-created (json/write-str (get-users text))
  :available-media-types ["application/json"])

(defresource delete-user [id]
  :allowed-methods [:delete]
  :handle-malformed "username cannot be empty"
  :delete!  (db/delete-user id)
  :handle-created (json/write-str "ok")
  :available-media-types ["application/json"])

(defroutes user-routes
;;   (GET "/ds" [] (restrict admin-view {:handler admin}))
  (GET "/users" request (get-search-users request))
  (POST "/users" [text] (search-users text))
  (DELETE "/user" [id] (delete-user id))
  (GET "/user/:id" request (get-user request))

  )
