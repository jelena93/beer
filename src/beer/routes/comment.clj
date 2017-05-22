(ns beer.routes.comment
  (:require [compojure.core :refer :all]
            [beer.models.db :as db]
            [liberator.core :refer [defresource]]
            [buddy.auth :refer [authenticated?]]
            [clojure.data.json :as json]
            [struct.core :as st]))

(def comment-schema
  {:user [st/required st/number]
   :beer [st/required st/number]
   :comment [st/required st/string]})

(defn comment-validaton? [params]
  (st/valid? params comment-schema))

(defn authenticated-admin? [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn render-map-generic [data]
  (json/write-str data :value-fn (fn [key value] (if (instance? java.sql.Date value) (str value) value))))

(defresource add-comment [{:keys [params session]}]
  :allowed-methods [:post]
  :malformed? (fn [_] (not (comment-validaton? (update (assoc params :user (:id (:identity session))) :beer read-string))))
  :handle-malformed (fn [_] "All fields are required")
  :exists? (fn [_] (not (empty? (db/find-beer-by-id (:beer params)))))
  :handle-not-found (fn [_] (str "There is no beer with id: " (:beer params)))
  :authorized? (authenticated? session)
  :new? true
  :respond-with-entity? true
  :post! (fn [_] (db/add-comment (assoc params :user (:id (:identity session)))))
  :handle-created (fn [_] (render-map-generic (db/get-comments (:beer params))))
  :available-media-types ["application/json"])

(defresource delete-comment [{:keys [params session]}]
  :allowed-methods [:delete]
  :malformed? (fn [_] (empty? (:id params)))
  :handle-malformed (fn [_] "Please provide an id")
  :exists? (fn [_] (not (empty? (db/find-comment-by-id (:id params)))))
  :handle-not-found (fn [_] (str "There is no comment with id: " (:id params)))
  :authorized? (authenticated-admin? session)
  :new? false
  :respond-with-entity? true
  :delete! (fn [_] (db/delete-comment (:id params)))
  :handle-ok (fn [_] (json/write-str (count (db/get-comments (:beer params)))))
  :available-media-types ["application/json"])

(defroutes comment-routes
  (POST "/comment" request (add-comment request))
  (DELETE "/comment/:id" request (delete-comment request)))
