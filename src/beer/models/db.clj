(ns beer.models.db
  (:require [clojure.java.jdbc :as sql]
            [korma.core :as k]
            [korma.db :refer [defdb mysql]]
            [clojure.set :as cs]
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

(defn get-user [id]
  (k/select  user
  (k/where {:id id})))

(defn delete-user [id]
  (k/delete user
  (k/where {:id id})))

(defn find-user [params]
  (k/select user
  (k/where params)))

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
          (k/where (or {:id text}
             (like :name (get-text-search text))
             (:origin text)
             (:price text)
             (:style text)
             (like :alcohol (get-text-search text))
             (like :country (get-text-search text))
             (like :manufacturer (get-text-search text))
             (like :country (get-text-search text))
             (like :info (get-text-search text))))
         (k/order :id :ASC)))

(defn get-styles []
  (k/select style
            (k/order :id :ASC)))

(defn update-style [params]
  (k/update style
          (k/set-fields {:description (:description params)})
          (k/where {:id (:id params)})))

(defn search-styles [text]
 (k/select style
  (k/where (or {:id text}
               (like :name (get-text-search text))
               (like :description (get-text-search text))))
         (k/order :id :ASC)))

(defn find-style-by-id [id]
  (k/select style
  (k/where {:id id})))

(defn find-style-by-name [bs-name]
  (k/select style
  (k/where {:name bs-name})
          (k/order :id :ASC)))

(defn find-beer-by-id [id]
  (k/select beer
        (k/fields :* [:style.name :sname])
        (k/join style (= :style :style.id))
  (k/where {:id id})))

(defn get-beers-for-style [bs-id]
  (k/select beer
          (k/where {:style bs-id})
          (k/order :beer.id :ASC)))

(defn find-beer-by-style-origin-price [bs-id origin price]
  (k/select beer
          (k/where {:style bs-id :origin origin :price price})
          (k/order :beer.id :ASC)))

(defn add-like [params]
  (k/insert likes
  (k/values params)))

(defn delete-like [params]
  (k/delete likes
  (k/where params)))

(defn get-likes [beer]
  (k/select  likes
  (k/where {:beer beer})))

(defn get-likes-count []
  (k/select  likes
          (k/fields :beer.name)
          (k/aggregate (count :*) :number)
          (k/group :beer)
          (k/join beer (= :beer :beer.id))))

(defn find-user-like-for-beer [beer user]
  (k/select likes
  (k/where {:beer beer :user user})))

(defn get-like [params]
  (k/select likes
  (k/where params)))

(defn add-comment [params]
  (k/insert comments
            (k/values (assoc params :date (c/to-sql-time (t/now))))))

(defn get-comments [beer]
  (k/select comments
          (k/fields :* :user.name :user.surname)
          (k/join user (= :user :user.id))
          (k/where {:beer beer})
          (k/order :id :ASC)))

(defn find-comment-by-id [id]
  (k/select comments
  (k/where {:id id})))

(defn get-comments-count []
  (k/select comments
          (k/fields :beer.name)
          (k/aggregate (count :*) :number)
          (k/group :beer)
          (k/join beer (= :beer :beer.id))))

(defn delete-comment [id]
  (k/delete comments
  (k/where {:id id})))
