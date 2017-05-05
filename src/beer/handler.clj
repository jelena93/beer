(ns beer.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.middleware :refer [wrap-base-url]]
            [selmer.parser :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [beer.routes.auth :refer [auth-routes]]
            [beer.routes.home :refer [home-routes]]
            [beer.routes.questions :refer [question-routes]]
            [beer.routes.bs :refer [bs-routes]]
            [beer.routes.beer :refer [beer-routes]]
            [beer.models.db :as db]))

(def backend (session-backend))

(defn init []
  (println "beer is starting")
  (selmer.parser/cache-off!))

(defn destroy []
  (println "beer is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes auth-routes home-routes question-routes bs-routes beer-routes app-routes)
      (handler/site)
      (wrap-authentication backend)
      (wrap-base-url)))
