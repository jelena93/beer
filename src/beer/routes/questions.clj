(ns beer.routes.questions
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
                        [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]]
            [liberator.core :refer [resource defresource]]
            [cheshire.core :refer [generate-string]]
            [beer.models.rule :as r])
    (:import [beer.models.question Question]))
(deftype  Qu [text answer ])

(defn get-question-page []
  (render-file "templates/question.html" {:title "Questions"}))

(defresource get-question [answer]
  :allowed-methods [:post]
  :handle-malformed "user name cannot be empty!"
  :post!  (println answer)
  :handle-created (generate-string (Qu. 42 false))
  :available-media-types ["application/json"])

(defroutes question-routes
  (GET "/questions" [] (get-question-page))
  (POST "/questions" [answer] (get-question answer)))


;; (defroutes question-routes
;;   (ANY "/questions" [] (resource :available-media-types ["application/json"]
;;                  :exists? (fn [ctx]
;;                             (= "tiger" (get-in ctx [:request :params "word"])))
;;                  :handle-ok "You found the secret word!"
;;                  :handle-not-found "Uh, that's the wrong word. Guess again!")))
