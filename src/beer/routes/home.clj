(ns beer.routes.home
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [selmer.parser :refer [render-file]]
            [compojure.response :refer [render]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [beer.models.db :as db]
            [ring.util.response :refer [response redirect content-type]]))

(defn authenticated [session]
  (authenticated? session))

(defn check-authenticated-admin [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn home [session]
  (cond
    (not (authenticated session))
     (redirect "/login")
    (check-authenticated-admin session)
     (render-file "templates/home-admin.html"
                 {:title "Home"
                  :logged (:identity session)})
    :else
     (render-file "templates/home-user.html"
                 {:title "Home"
                  :logged (:identity session)
                  :beers (db/get-beers)})))


(defroutes home-routes
  (GET "/" request (home (:session request))))
