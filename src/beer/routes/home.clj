(ns beer.routes.home
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [selmer.parser :refer [render-file]]
            [compojure.response :refer [render]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [response redirect content-type]]))

(defn authenticated [session]
  (authenticated? session))

(defn admin [req]
  (and (authenticated? req)
       (#{:admin} (:role (:identity req)))))

(defn home [session]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/home.html" {:title "Home" :logged (:identity session)})))

(defroutes home-routes
  (GET "/" request (home (:session request)))
  )
