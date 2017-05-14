(ns beer.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [clojure.string :refer [blank?]]
            [ring.util.response :refer [redirect]]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]))

(defn login-page [&[error]]
  (render-file "templates/login.html" {:title "Login" :error error}))

(defn handle-login [request]
  (let [username (:username (:params request))
        password (:password (:params request))
        user (first (db/find-user (:params request)))
        session (:session request)]
    (cond
      (or (nil? username) (nil? password))
      (login-page "Please provide username and password")
      (empty? user)
      (login-page "Bad credentials")
      :else
      (do (assoc (redirect "/"):session (assoc session :identity user))))))

(defn registration-page [&[error]]
  (render-file "templates/register.html" {:title "Register" :error error}))

(defn handle-registration [request]
  (let [username (:username (:params request))
        password (:password (:params request))
        first_name (:first_name (:params request))
        last_name (:last_name (:params request))
        email (:email (:params request))
        session (:session request)]
  (if (or (blank? username) (blank? password) (blank? first_name) (blank? last_name) (blank? email))
    (registration-page "Please fill out all fields")
  (do
    (try
      (db/add-user (assoc (:params request) :role "user"))
      (assoc (redirect "/"):session (assoc session :identity username))
      (catch Exception  e (render-file "templates/register.html" {:title "Register"
:error (str e "User with username: " username " already exists") })))))))

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
