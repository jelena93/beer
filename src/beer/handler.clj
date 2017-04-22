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
            [noir.session :as session]
            [noir.validation
             :refer [wrap-noir-validation]]
            [ring.middleware.session.memory
             :refer [memory-store]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [beer.routes.home :refer [home-routes]]
            [beer.routes.auth :refer [auth-routes]]
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
  (-> (routes auth-routes home-routes app-routes)
      (handler/site)
      (session/wrap-noir-session
        {:store (memory-store)})
      (wrap-noir-validation)
      (wrap-authentication backend)
          (wrap-params)
    (wrap-session)
      (wrap-base-url)))
