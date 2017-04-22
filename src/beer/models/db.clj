(ns beer.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db {:subprotocol "mysql"
         :subname "//127.0.0.1:3306/beer_es"
         :user "admin"
         :password "admin"})

(defn add-user [username password first_name last_name email]
  (let [sql "insert into user (username, password, first_name, last_name, email, role) values (?, ?, ?, ?, ?, ?)"]
    (sql/with-connection
      db
      (sql/do-prepared sql [username password first_name last_name email "USER"] ))))

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


