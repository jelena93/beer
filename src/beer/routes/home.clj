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
            [clojure.java.io :as io]
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
  (println (authenticated? session))
  (if-not (authenticated? session)
    (redirect "/login")
  (render-file "templates/home.html" {:title "Home"})))

(defn save-message [name message]
  (cond
    (empty? name)
    (home name message "No name")
    (empty? message)
    (home name message "No message")
    :else
    (do
      (db/save-message name message)
      (home))))

(defroutes home-routes
  (GET "/" request (home (:session request)))
  (POST "/" [name message] (save-message name message)))
