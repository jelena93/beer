(ns beer.routes.home
  (:require [compojure.core :refer :all]
            [beer.views.layout :as layout]
            [hiccup.form :refer :all]
            [beer.models.question :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
                        [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
                        [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]]
            [beer.models.rule :as r])
  (:import [beer.models.question Question]))

(defn format-time [timestamp]
  (-> "dd.MM.yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn show-beer []
  [:ul.beer
   (for [{:keys [beerid beername]} (db/read-beer)]
     [:li
      [:blockquote beerid]
      [:p "-" [:cite beername]]])])

(def q (Question. nil 42 [1 2] nil false))

;; (defn home [& [name message error]]
;;   (println "pre: " q)
;;   (r/asd q)
;;   (println "posle: " q)
;;   (render-file "templates/home.html" {:title "Beer" :test q}))

(defn home [session]
  (if-not (authenticated? session)
    (redirect "/login")
  (render-file "templates/home.html" {:title "Home"})))

(defroutes home-routes
  (GET "/" request (home (:session request))))
