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
            [clojure.data.json :as json]
            [beer.models.rule :as rules])
    (:import [beer.models.question Question]))

;(def q (Question. nil nil nil nil false nil))

(defn get-question-as-map [text suggestedAnswers]
  {:text text :suggestedAnswers suggestedAnswers})

(defn get-question-page []
  (def q (Question. nil nil nil nil false nil))
  (rules/askQuestion q)
  (render-file "templates/question.html" {:title "Questions" :question (get-question-as-map (.getText q) (.getSuggestedAnswers q))}))

(defn get-question-from-rules [answer]
  (.setAnswer q answer)
  (rules/askQuestion q))

(defresource get-question [answer]
  :allowed-methods [:post]
  :handle-malformed "user name cannot be empty!"
  :post!  (get-question-from-rules answer)
  :handle-created (json/write-str (get-question-as-map (.getText q) (.getSuggestedAnswers q)))
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
