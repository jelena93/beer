(ns beer.routes.questions
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
                        [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]]
            [beer.models.rule :as r]))

(defn get-question []
  (render-file "templates/question.html" {:title "Questions"}))


(defroutes question-routes
  (GET "/questions" request (get-question)))
