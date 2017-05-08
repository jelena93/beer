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
;;             [buddy.auth.accessrules :refer (success error)]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [ring.util.response :refer (response redirect)]
            [beer.routes.auth :refer [auth-routes]]
            [beer.routes.home :refer [home-routes]]
            [beer.routes.questions :refer [question-routes]]
            [beer.routes.bs :refer [bs-routes]]
            [beer.routes.beer :refer [beer-routes]]))

;; (defn is-admin [{:keys [params] session :session}]
;;   (println "wtf" session)
;;   (contains? (apply hash-set (:role session)) "ADMIN"))
(defn is-admin [request]
  (contains? (apply hash-set (:role request)) "ADMIN"))

(def rules [{:pattern #"^/admin/.*"
             :handler is-admin}
;;             {:pattern #"^/login$"
;;              :handler any-access}
;;             {:pattern #"^/.*"
;;              :handler authenticated-access}
            ])
;; (def rules
;;   [{:uri "/restricted"
;;     :handler authenticated?}])

;; (def rules [{:pattern #"^/admin/.*"
;;              :handler {:or [admin-access operator-access]}
;;              :redirect "/notauthorized"}
;;             {:pattern #"^/login$"
;;              :handler any-access}
;;             {:pattern #"^/.*"
;;              :handler authenticated-access
;;              :on-error (fn [req _] (response "Not authorized ;)"))}])

(defn on-error
  [request value]
  {:status 403
   :headers {}
   :body "Not authorized"})

;; (defn unauthorized-handler
;;   [request metadata]
;;   (-> (response "Unauthorized request")
;;       (assoc :status 403)))

(defn home-controller
  [{:keys [params] session :session}]
  (println "home" session)
  (when (not (authenticated? session))
    (redirect "/login")))

(def backend (session-backend))

(defn init []
  (selmer.parser/cache-off!))

(defn destroy [])

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes auth-routes home-controller home-routes question-routes bs-routes beer-routes app-routes)
      (handler/site)
      (wrap-access-rules {:rules rules :on-error on-error})
      (wrap-authentication backend)
      (wrap-authorization backend)
      (wrap-base-url)))
