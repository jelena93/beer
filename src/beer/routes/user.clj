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
  (cond
    (not= (authenticated session))
    (redirect "/login")
    (not= (:id params) (str (:id (:identity session))))
    (redirect "/")
    :else
    (render-file "templates/user-edit.html"
                 {:title (str "User " (:id params)) :logged (:identity session) :user (first (db/get-user (:id params)))})))

(defresource search-users [{:keys [params session] request :request}]
  :allowed-methods [:post]
  :authenticated? (check-authenticated-admin session)
  :handle-created (json/write-str (get-users (:text params)))
  :available-media-types ["application/json"])

(defresource delete-user [{:keys [params session] request :request}]
  :allowed-methods [:delete]
  :handle-malformed "username cannot be empty"
  :authenticated? (authenticated request)
  :delete! (db/delete-user (:id (:identity session)))
  :handle-created (json/write-str "ok")
  :available-media-types ["application/json"])

(defresource edit-user [{:keys [params session] request :request}]
  :allowed-methods [:put]
  :handle-malformed "all field are required"
  :authenticated? (authenticated request)
  :put!  (db/update-user (:id (:identity session)) (:username params) (:password params) (:first_name params) (:last_name params) (:email params))
  :handle-created (json/write-str "User successfully edited")
  :available-media-types ["application/json"])

(defroutes user-routes
  (GET "/users" request (get-search-users request))
  (POST "/users" request (search-users request))
  (DELETE "/user/:id" request (delete-user request))
  (PUT "/user/:id" request (edit-user request))
  (GET "/user/:id" request (get-user request)))
