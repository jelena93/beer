(ns beer.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [beer.views.layout :as layout]
            [ring.util.response :refer [redirect]]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [hiccup.form :refer [form-to label text-field password-field submit-button]]))

(defn login-page [&[error]]
  (render-file "templates/login.html" {:title "Login" :error error}))

(defn handle-login [request]
  (let [username (:username (:params request))
        password (:password (:params request))
        user (db/get-user username password)
        session (:session request)]
    (cond
      (or (nil? username) (nil? password))
      (login-page "Please provide username and password")
      (nil? user)
      (login-page "Bad credentials")
      :else
      (do (assoc (redirect "/"):session (assoc session :identity username))))))

(defn registration-page [&[error]]
  (render-file "templates/register.html" {:title "Register" :error error}))

(defn handle-registration [request]
  (let [username (:username (:params request))
        password (:password (:params request))
        first_name (:first_name (:params request))
        last_name (:last_name (:params request))
        email (:email (:params request))
        session (:session request)]
  (if (or (nil? username) (nil? password) (nil? first_name) (nil? last_name) (nil? email))
    (login-page "Please fill all fields"))
  (do
    (try
      (db/add-user username password first_name last_name email)
      (assoc (redirect "/"):session (assoc session :identity username))
      (catch Exception  e (render-file "templates/register.html" {:title "Register" :error (str "User with username: " username " already exists") }))))))

(defn logout
  [request]
  (-> (redirect "/login")
      (assoc :session {})))

(defroutes auth-routes
  (GET "/login" [] (login-page))
  (POST "/login" request
        (handle-login request))
  (GET "/logout" request (logout request))
  (GET "/register" [] (registration-page))
  (POST "/register" request (handle-registration request)))
