(ns beer.routes.auth
  (:require [compojure.core :refer :all]
            [bcrypt-clj.auth :as bcrypt]
            [struct.core :as st]
            [ring.util.response :refer [redirect]]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]))

(def user-login-schema
  {:username [st/required st/string]
   :password [st/required st/string]})

(def user-register-schema
  {:username [st/required st/string]
   :password [st/required st/string]
   :name [st/required st/string]
   :surname [st/required st/string]
   :email [st/required st/email]})

(defn login-validaton? [params]
  (st/valid? {:username (:username params)
              :password (:password params)} user-login-schema))

(defn register-validaton? [params]
  (st/valid? {:username (:username params)
              :password (:password params)
              :name (:name params)
              :surname (:surname params)
              :email (:email params)} user-register-schema))

(defn get-login-page [&[error]]
  (render-file "templates/login.html" {:title "Login"
                                       :error error}))
(defn registration-page [&[error]]
  (render-file "templates/register.html" {:title "Register"
                                          :error error}))

(defn get-user-by-username-from-db [params]
  (-> (select-keys params [:username])
      (db/find-user)
      (first)))

(defn handle-login [{:keys [params session]}]
  (let [user (get-user-by-username-from-db params)]
    (cond
      (not (login-validaton? params))
      (get-login-page "Please provide username and password")
      (or (empty? user)
          (not (bcrypt/check-password (:password params) (:password user))))
      (get-login-page "Bad credentials")
      :else
      (do (assoc (redirect "/"):session (assoc session :identity user))))))

(defn add-user-to-db [params]
  (db/add-user (assoc params :role "user"))
  (first (db/find-user params)))

(defn save-user [params]
  (->(update-in params [:password] bcrypt/crypt-password)
     (add-user-to-db)))

(defn handle-registration [{:keys [params session]}]
  (let [same-user (get-user-by-username-from-db params)]
    (cond
      (not (register-validaton? params))
      (registration-page "Please fill out all fields")
      (not-empty same-user)
      (registration-page (str "User with username: " (:username params) " already exists"))
      :else
      (assoc (redirect "/"):session (assoc session :identity (save-user params))))))

(defn logout [request]
  (-> (redirect "/login")
      (assoc :session {})))

(defroutes auth-routes
  (GET "/login" [] (get-login-page))
  (POST "/login" request (handle-login request))
  (GET "/logout" request (logout request))
  (GET "/register" [] (registration-page))
  (POST "/register" request (handle-registration request)))
