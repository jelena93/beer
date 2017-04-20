(ns beer.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [beer.views.layout :as layout]
            [beer.models.db :as db]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [noir.validation :refer [rule errors? has-value? on-error]]
            [noir.util.crypt :as crypt]
            [hiccup.form :refer [form-to label text-field password-field submit-button]]))

(defn format-error [[error]]
  [:p.error error])

(defn control [field name text]
  (list (on-error name format-error)
        (label name text)
        (field name)
        [:br]))

(defn login-page []
  (layout/common
    (form-to [:post "/login"]
             (control text-field :id "Screen name")
             (control password-field :pass "Password")
             (submit-button "login"))))

(defn handle-login [id pass]
  (let [user (db/get-user id pass)]
    (rule (has-value? id)
          [:id "Screen name is required"])
    (rule (has-value? pass)
          [:pass "Password is required"])
    (cond
      (errors? :id :pass)
      (login-page)
      (nil? user)
      (login-page)
      :else
      (do
        (session/put! :user user)
        (redirect "/")))))

(defn registration-page []
  (layout/common
    (form-to [:post "/register"]
             (control text-field :id "Screen name")
             (control password-field :pass "Password")
             (control password-field :pass1 "Retype Password")
             (submit-button "Create Account"))))

(defn handle-registration [id pass pass1]
  (rule (= pass pass1)
        [:pass "Password was not retyped correctly"])
  (if (errors? :pass)
    (registration-page)
    (do
      (db/add-user id pass)
      (redirect "/"))))

(defn logout []
  (layout/common
    (form-to [:post "/logout"]
             (submit-button "logout"))))

(defroutes auth-routes
  (GET "/login" [] (login-page))
  (POST "/login" [id pass]
        (handle-login id pass))
  (GET "/logout" [] (logout))
  (POST "/logout" []
        (session/clear!)
        (redirect "/"))
  (GET "/register" [_] (registration-page))
  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1)))
