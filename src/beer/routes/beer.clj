(ns beer.routes.beer
  (:require [compojure.core :refer :all]
            [hiccup.form :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
                        [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
                        [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]])
)

(defn get-beer [id]
  (println (db/find-beer-by-id id))
  (render-file "templates/beer.html" {:beer (first (db/find-beer-by-id id))}))

(defroutes beer-routes
  (GET "/beer/:id" [id] (get-beer id)))
