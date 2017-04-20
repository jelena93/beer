(ns beer.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db {:subprotocol "mysql"
         :subname "//127.0.0.1:3306/beer_es"
         :user "admin"
         :password "admin"})

(defn add-user [username password]
  (let [sql "insert into user (username, password) values (? , ?)"]
    (sql/with-connection
      db
      (sql/do-prepared sql [username password] ))))

(defn get-user [username password]
  (sql/with-connection
    db
    (sql/with-query-results
      res ["select * from user where username = ? and password = ?" username password] (first res))))

;;(db/delete-user ((session/get :user):username))
(defn delete-user [username]
  (let [sql "delete from user where username = ?"]
    (sql/with-connection
      db
      (sql/do-prepared sql [username] ))))

(defn read-beer []
  (sql/with-connection
    db
    (sql/with-query-results res
      ["SELECT * FROM beer"]
      (doall res))))

(defn save-message [name message]
  (sql/with-connection
    db
    (sql/insert-values
      :beer
      [:name :message :timestamp]
      [name message (new java.util.Date)])))
