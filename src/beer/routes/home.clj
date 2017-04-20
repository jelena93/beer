(ns beer.routes.home
  (:require [compojure.core :refer :all]
            [beer.views.layout :as layout]
            [hiccup.form :refer :all]
            [selmer.parser :refer [render-file]]
            [noir.session :as session]
            [beer.models.db :as db]
            [beer.models.rule :as r]))

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

(defn home [& [name message error]]
  (render-file "templates/home.html" {:title "Beer" :test (r/asd)}))

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
  (GET "/" [] (home))
  (POST "/" [name message] (save-message name message)))
