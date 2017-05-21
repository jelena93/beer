(ns beer.routes.statistics
  (:require [compojure.core :refer :all]
            [buddy.auth :refer [authenticated?]]
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

(defn get-cols [stats]
 (for [s stats]
      {:label (:name s) :type "string"}))

(defn get-all-cols [stats]
  (into [] (concat [{:label "Beer" :type "string"}] (get-cols stats))))

(defn get-rows [stats]
 (for [s stats]
      {:c [{:v (:name s)} {:v (:number s)}]}))

(defn get-likes []
  (let [likes (db/get-likes-count)]
    (assoc {} :cols (get-all-cols likes) :rows (get-rows likes))))

(defn get-comments []
  (let [comments (db/get-comments-count)]
    (assoc {} :cols (get-all-cols comments) :rows (get-rows comments))))

(defresource get-stats-likes [{:keys [params session]}]
  :allowed-methods [:get]
  :authorized? (fn [_] (authenticated-admin? session))
  :handle-ok (fn [_] (json/write-str (get-likes)))
  :available-media-types ["application/json"])

(defresource get-stats-comments [{:keys [params session]}]
  :allowed-methods [:get]
  :authorized? (fn [_] (authenticated-admin? session))
  :handle-ok (fn [_] (json/write-str (get-comments)))
  :available-media-types ["application/json"])

(defroutes stats-routes
  (GET "/stats/likes" request (get-stats-likes request))
  (GET "/stats/comments" request (get-stats-comments request)))
