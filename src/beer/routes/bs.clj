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


;; (defn show-beer []
;;   [:ul.beer
;;    (for [{:keys [beerid beername]} (db/read-beer)]
;;      [:li
;;       [:blockquote beerid]
;;       [:p "-" [:cite beername]]])])

;; (defn home [session]
;;   (if-not (authenticated? session)
;;     (redirect "/login")
;;   (render-file "templates/home.html" {:title "Home"})))
(defn get-bs [id]
  (render-file "templates/bs.html" {:bs (db/get-beer-style-by-id id) :beers (db/get-beers-for-beer-style id)})
  )

(defroutes bs-routes
  (GET "/bs/:id" [id] (get-bs id)))
