(ns beer.models.db
  (:require [clojure.java.jdbc :as sql]
            [korma.core :as k]
            [korma.db :refer [defdb mysql]]
            [clj-time.coerce :as c]
            [clj-time.core :as t])
  (:import java.sql.DriverManager))

(def db-config (clojure.edn/read-string (slurp "conf/db-config.edn")))

(defdb db (mysql db-config))

(k/defentity user
  (k/table :user))

(k/defentity style
  (k/table :style))

(k/defentity beer
  (k/table :beer))

(k/defentity likes
  (k/table :likes))

(k/defentity comments
  (k/table :comments))

(defn get-text-search [text]
  (str "%" text "%"))

(defn add-user [params]
  (k/insert user
  (k/values params)))

(defn delete-user [id]
  (k/delete user
  (k/where {:id id})))

(defn find-user [params]
  (k/select user
            (k/where params)
            (k/order :id :ASC)))

(defn get-users []
  (k/select user
  (k/where {:role "user"})
  (k/order :id :ASC)))

(defn update-user [params]
  (k/update user
            (k/set-fields params)
            (k/where {:id (:id params)})))

(defn add-beer [params]
  (k/insert beer
  (k/values params)))

(defn update-beer [params]
  (k/update beer
            (k/set-fields params)
            (k/where {:id (:id params)})))

(defn delete-beer [id]
  (k/delete beer
  (k/where {:id id})))

(defn get-beers []
  (k/select beer
          (k/fields :* [:style.name :sname])
          (k/join style (= :style :style.id))
          (k/order :id :ASC)))

(defn search-beers [text]
  (k/select beer
            (k/fields :* [:style.name :sname])
            (k/join style (= :style :style.id))
            (k/where (or
                       {:id text}
                       {:name [like (get-text-search text)]}
                       {:style.name [like (get-text-search text)]}
                       {:alcohol text}
                       {:country [like (get-text-search text)]}
                       {:manufacturer [like (get-text-search text)]}))
            (k/order :id :ASC)))

(defn find-beer [params]
  (k/select beer
            (k/fields :* [:style.name :sname])
            (k/join style (= :style :style.id))
            (k/where params)
            (k/order :id :ASC)))

(defn get-styles []
  (k/select style
            (k/order :id :ASC)))

(defn update-style [{:keys [id description]}]
  (k/update style
          (k/set-fields {:description description})
          (k/where {:id id})))

(defn search-styles [text]
  (k/select style
            (k/where (or
                       {:id text}
                       {:name [like (get-text-search text)]}))
            (k/order :id :ASC)))

(defn find-style [params]
  (k/select style
            (k/where params)
            (k/order :id :ASC)))

(defn add-like [params]
  (k/insert likes
  (k/values params)))

(defn delete-like [params]
  (k/delete likes
  (k/where params)))

(defn get-likes-count []
  (k/select likes
          (k/fields :beer.name)
          (k/aggregate (count :*) :number)
          (k/group :beer)
          (k/join beer (= :beer :beer.id))))

(defn find-like [params]
  (k/select likes
  (k/where params)))

(defn add-comment [params]
  (k/insert comments
            (k/values (assoc params :date (c/to-sql-time (t/now))))))

(defn find-comment [params]
  (k/select comments
          (k/fields :* :user.name :user.surname)
          (k/join user (= :user :user.id))
          (k/where params)
          (k/order :id :ASC)))

(defn get-comments-count []
  (k/select comments
          (k/fields :beer.name)
          (k/aggregate (count :*) :number)
          (k/group :beer)
          (k/join beer (= :beer :beer.id))))

(defn delete-comment [id]
  (k/delete comments
  (k/where {:id id})))
