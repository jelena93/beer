(ns beer.routes.questions
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [beer.models.beer :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [buddy.auth :refer [authenticated?]]
            [beer.models.rule :as rules])
    (:import [beer.models.question Question]
             [beer.models.beer Beer]))

(defn authenticated [session]
  (authenticated? session))

(defn get-question-as-map [q]
  {:text (.getText q) :suggestedAnswers (.getSuggestedAnswers q) :isEnd (.isEnd q) :bs (.getIdBs q)})

(defn get-question-page [{:keys [params session] request :request}]
   (if-not (authenticated session)
    (redirect "/login")
     (do (def q (->Question nil nil nil nil false nil nil nil nil nil nil))
       (rules/ask-question q)
       (render-file "templates/question.html" {:title "Questions" :logged (:identity session) :question (get-question-as-map q)}))))

(defn get-answer []
  (rules/ask-question (.getBeerStyle (.getBeer q)))
  (let [bs (db/find-beer-style-by-name (.getNameBs q))]
    (.setIdBs! q (:id bs))))

(defn get-question-from-rules [answer]
  (.setAnswer! q answer)
  (rules/ask-question q)
  (println "id: " (.getIdBs q))
  (if (.isEnd q)
      (get-answer)))

(defresource get-question [{:keys [params session] request :request}]
  :allowed-methods [:post]
  :handle-malformed "answer cannot be empty"
  :authenticated? (authenticated session)
  :post!  (get-question-from-rules (:answer params))
  :handle-created (json/write-str (get-question-as-map q))
  :available-media-types ["application/json"])

(defroutes question-routes
  (GET "/questions" request (get-question-page request))
  (POST "/questions" [answer] (get-question answer)))



;; (defroutes question-routes
;;   (ANY "/questions" [] (resource :available-media-types ["application/json"]
;;                  :exists? (fn [ctx]
;;                             (= "tiger" (get-in ctx [:request :params "word"])))
;;                  :handle-ok "You found the secret word!"
;;                  :handle-not-found "Uh, that's the wrong word. Guess again!")))
