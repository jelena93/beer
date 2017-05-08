(ns beer.routes.bs
  (:require [compojure.core :refer :all]
            [hiccup.form :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
                        [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
                        [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]])
  (:import [beer.models.question Question]))


(defn get-bs [id]
  (render-file "templates/bs.html" {:bs (first (db/find-beer-style-by-id id)) :beers (db/get-beers-for-beer-style id)})
  )

(defroutes bs-routes
  (GET "/bs/:id" [id] (get-bs id)))
