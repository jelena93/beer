(ns beer.routes.statistics
  (:require [compojure.core :refer :all]
            [buddy.auth :refer [authenticated?]]
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

(defn get-cols [stats]
 (for [s stats]
      {:label (:name s) :type "string"}))

(defn get-rows [stats]
 (for [s stats]
      {:c [{:v (:name s)} {:v (:number s)}]}))

(defn get-likes []
  (let [beer-likes (db/get-beer-likes-count)]
    (assoc {} :cols (get-cols beer-likes) :rows (get-rows beer-likes))))

(defn get-comments []
  (let [beer-comments (db/get-beer-comments-count)]
    (assoc {} :cols (get-cols beer-comments) :rows (get-rows beer-comments))))

(defresource get-stats-likes [{:keys [params session] request :request}]
  :allowed-methods [:get]
  :authenticated? (not (check-authenticated-admin session))
  :handle-ok (json/write-str (get-likes))
  :available-media-types ["application/json"])

(defresource get-stats-comments [{:keys [params session] request :request}]
  :allowed-methods [:get]
  :authenticated? (not (check-authenticated-admin session))
  :handle-ok (json/write-str (get-comments))
  :available-media-types ["application/json"])

(defroutes stats-routes
  (GET "/stats/likes" request (get-stats-likes request))
  (GET "/stats/comments" request (get-stats-comments request)))
