(ns beer.routes.user
  (:require [compojure.core :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [ring.util.response :refer [response redirect content-type]]))

(defn authenticated [session]
  (authenticated? session))

(defn authenticated-admin [session]
  (if (and (not (authenticated? session))
       (not="admin" (:role (:identity session))))
    (throw-unauthorized {:message "Not authorized"})))

(defn check-authenticated-admin [session]
  (and (not (authenticated? session))
       (not="admin" (:role (:identity session)))))

(defn authenticated-same-user [{:keys [params session] request :request}]
  (and (authenticated? session) (= (:username session) (:username params))))

(defn get-users [text]
  (if (or (nil? text) (= "" text))
    (db/get-users)
    (db/search-users (str "%" text "%"))))

(defn get-search-users [{:keys [params session] request :request}]
(authenticated-admin session)
  (render-file "templates/user-search.html" {:title "Search users" :logged (:identity session) :users (get-users nil)}))

(defn add-user [{session :session}]
  (render-file "templates/add-user.html" {:title "Add user" :logged (:identity session)}))

(defn get-user [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
  (render-file "templates/user.html" {:title (str "User " (:id params)) :logged (:identity session) :user (db/get-user (:id params))})))

(defresource search-users [{:keys [params session] request :request}]
  :allowed-methods [:post]
  :authenticated? (check-authenticated-admin session)
  :handle-created (json/write-str (get-users (:text params)))
  :available-media-types ["application/json"])

(defresource delete-user [{:keys [params session] request :request}]
  :allowed-methods [:delete]
  :handle-malformed "username cannot be empty"
  :authenticated? (authenticated session)
  :delete!  (db/delete-user (:id params))
  :handle-created (json/write-str "ok")
  :available-media-types ["application/json"])

(defresource edit-user [{:keys [params session] request :request}]
  :allowed-methods [:delete]
  :handle-malformed "username cannot be empty"
  :authenticated? (authenticated-same-user request)
  :delete!  (db/update-user (:id session) (:username params) (:password params) (:first-name params) (:last-name params))
  :handle-created (json/write-str "ok")
  :available-media-types ["application/json"])

(defroutes user-routes
  (GET "/users" request (get-search-users request))
  (POST "/users" request (search-users request))
  (DELETE "/user" request (delete-user request))
  (PUT "/user" request (edit-user request))
  (GET "/user/:id" request (get-user request)))
