(ns beer.routes.home
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [selmer.parser :refer [render-file]]
            [compojure.response :refer [render]]
            [buddy.auth :refer [authenticated?]]
            [beer.models.db :as db]
            [ring.util.response :refer [redirect]]))

(defn authenticated-admin? [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn get-home-page [page session]
  (render-file page
               {:title "Home"
                :logged (:identity session)
                :beers (db/get-beers)}))

(defn home [session]
  (cond
    (not (authenticated? session))
    (redirect "/login")
    (authenticated-admin? session)
    (get-home-page "templates/home-admin.html" session)
    :else
    (get-home-page "templates/home-user.html" session)))

(defroutes home-routes
  (GET "/" request (home (:session request))))
