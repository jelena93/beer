(ns beer.routes.home
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]]
            [beer.models.rule :as r])
  (:import [beer.models.question Question]))

(defn home [session]
  (render-file "templates/home.html" {:title "Home"}))

(defroutes home-routes
  (GET "/" request (home (:session request))))
