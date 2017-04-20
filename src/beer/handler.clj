(ns beer.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [noir.session :as session]
            [noir.validation
             :refer [wrap-noir-validation]]
            [ring.middleware.session.memory
             :refer [memory-store]]
            [beer.routes.home :refer [home-routes]]
            [beer.routes.auth :refer [auth-routes]]
            [beer.models.db :as db]))

(defn init []
  (println "beer is starting"))

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
      (wrap-base-url)))
