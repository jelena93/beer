(ns beer.routes.like
  (:require [compojure.core :refer :all]
            [beer.models.db :as db]
            [buddy.auth :refer [authenticated?]]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [struct.core :as st]))

(defresource handle-like [{:keys [params session]}]
  :allowed-methods [:post :delete]
  :malformed? (fn [_] (empty? (:beer params)))
  :handle-malformed "Beer id cannot be empty"
  :authorized? (authenticated? session)
  :exists? (fn [_] (not (empty? (db/find-like (assoc params :user (:id (:identity session)))))))
  :handle-not-found (fn [_] (str "There is no beer with id: " (:beer params)))
  :respond-with-entity? true
  :post! (fn [_] (db/add-like (assoc params :user (:id (:identity session)))))
  :delete! (fn [_] (db/delete-like (assoc params :user (:id (:identity session)))))
  :handle-ok (fn [_] (json/write-str (count (db/find-like))))
  :handle-created (fn [_] (json/write-str (count (db/find-like params))))
  :available-media-types ["application/json"])


(defroutes like-routes
  (POST "/like" request (handle-like request))
  (DELETE "/like" request (handle-like request)))
