(ns beer.models.db
  (:require [clojure.java.jdbc :as sql]
            [clojure.string :as str]
            [korma.core :refer :all]
            [korma.db :refer :all]
            [clojure.set :as cs])
  (:import java.sql.DriverManager)
;;   (:use [korma.db]
;;         [korma.core])
  )

(defdb db (mysql {:db "beer"
                     :user "admin"
                     :password "admin"}))
(defentity user
  (table :user)
  (entity-fields :username :first_name :last_name :email :role)
  (prepare (fn [v] (cs/rename-keys v {:my-column :my_column})))
  (transform (fn [v] (cs/rename-keys v {:my_column :my-column}))))

(defentity beer-style
  (table :beer_style)
  (prepare (fn [v] (cs/rename-keys v {:my-column :my_column})))
  (transform (fn [v] (cs/rename-keys v {:my_column :my-column}))))

(defentity beer
  (table :beer)
  (prepare (fn [v] (cs/rename-keys v {:my-column :my_column})))
  (transform (fn [v] (cs/rename-keys v {:my_column :my-column}))))

(defn find-user [params]
  (select user
  (where params)
  (limit 1)))

(defn add-user [params]
  (insert user
  (values params)))

(defn find-beer-style-by-id [id]
  (select beer-style
  (where {:id id})
  (limit 1)))

(defn find-beer-style-by-name [bs-name]
  (select beer-style
  (where {:name bs-name})
  (limit 1)))

(defn find-beer-style-by-name [bs-name]
  (select beer-style
  (where {:name bs-name})
  (limit 1)))

(defn find-beer-by-id [id]
  (select beer-style
  (where {:id id})
  (limit 1)))

(defn find-beer-by-id [id]
  (select beer
  (where {:id id})))

(defn get-beers-for-beer-style [bs-id]
  (select beer
   (join beer-style)
  (where {:beer_style bs-id}))
;;   (order :beer.id :ASC)
  )

;; (defn get-beers-for-beer-style [id]
;;   (sql/with-connection
;;     db
;;     (sql/with-query-results
;;       res ["SELECT * FROM beer b JOIN beerstyle bs on (b.stil=bs.beerStyleID) WHERE bs.beerStyleID = ?" id] (doall res)))
;;   )

;;(db/delete-user ((session/get :user):username))
;; defn delete-user username
;; (defn find-user [username password]
;;   (select user
;;   (fields :username :first_name :last_name)
;;   (where {:username username :password password})
;;   (limit 1)))

;; (defn add-user [username password first_name last_name email role]
;;   (insert user
;;   (values {:username username :password password :first_name first_name :last_name last_name :email email :role role})))

