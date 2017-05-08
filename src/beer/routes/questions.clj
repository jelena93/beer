(ns beer.routes.questions
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [beer.models.beer :refer :all]
            [beer.models.beerStyle :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [beer.models.rule :as rules])
    (:import [beer.models.question Question]
             [beer.models.beer Beer]))

(defn get-question-as-map [q]
  {:text (.getText q) :suggestedAnswers (.getSuggestedAnswers q) :isEnd (.isEnd q) :bs (.getIdBs q)})

(defn get-question-page []
  (def q (->Question nil nil nil nil false nil nil nil nil nil nil))
  (rules/ask-question q)
  (render-file "templates/question.html" {:title "Questions" :question (get-question-as-map q)}))

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

(defresource get-question [answer]
  :allowed-methods [:post]
  :handle-malformed "user name cannot be empty!"
  :post!  (get-question-from-rules answer)
  :handle-created (json/write-str (get-question-as-map q))
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
