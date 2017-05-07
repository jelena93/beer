(ns beer.models.db
  (:require [clojure.java.jdbc :as sql]
            [yesql.core :refer [defqueries]])
  (:import java.sql.DriverManager))

(def db {:subprotocol "mysql"
         :subname "//127.0.0.1:3306/beer"
         :user "root"
         :password ""})

(defqueries "sql/users.sql"
  {:connection db})


;; (defn add-user [username password first_name last_name email]
;;   (let [sql "insert into user (username, password, first_name, last_name, email, role) values (?, ?, ?, ?, ?, ?)"]
;;     (sql/with-connection
;;       db
;;       (sql/do-prepared sql [username password first_name last_name email "USER"])))
;;   )

(defn get-beer-style-by-name [bsname]
;;   (sql/with-connection
;;     db
;;     (sql/with-query-results
;;       res ["select * from beerstyle where name = ?" bsname] (first res)))
  )

(defn get-beer-style-by-id [id]
;;   (sql/with-connection
;;     db
;;     (sql/with-query-results
;;       res ["select * from beerstyle where beerStyleID = ?" id] (first res)))
  )

(defn get-beer-by-id [id]
;;   (sql/with-connection
;;     db
;;     (sql/with-query-results
;;       res ["select * from beer where beerID = ?" id] (first res)))
  )

(defn get-beers-for-beer-style [id]
;;   (sql/with-connection
;;     db
;;     (sql/with-query-results
;;       res ["SELECT * FROM beer b JOIN beerstyle bs on (b.stil=bs.beerStyleID) WHERE bs.beerStyleID = ?" id] (doall res)))
  )

;;(db/delete-user ((session/get :user):username))
(defn delete-user [username]
;;   (let [sql "delete from user where username = ?"]
;;     (sql/with-connection
;;       db
;;       (sql/do-prepared sql [username] )))
  )

(defn read-beer []
;;   (sql/with-connection
;;     db
;;     (sql/with-query-results res
;;       ["SELECT * FROM beer"]
;;       (doall res)))
  )


