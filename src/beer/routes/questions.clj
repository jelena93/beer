(ns beer.routes.questions
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [beer.models.beer :refer :all]
            [beer.models.beerStyle :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
                        [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [beer.models.rule :as rules])
    (:import [beer.models.question Question]
             [beer.models.beer Beer]
             [beer.models.beerStyle BeerStyle]
             ))

(defn get-question-as-map [q]
  {:text (.getText q) :suggestedAnswers (.getSuggestedAnswers q) :isEnd (.isEnd q) :bs (.getId (.getBeerStyle (.getBeer q)))})

(defn get-question-page []
  (def q (Question. nil nil nil nil false (Beer. nil nil nil nil nil nil nil nil (BeerStyle. 0 nil nil nil nil nil nil nil))))
  (rules/ask-question q)
  (render-file "templates/question.html" {:title "Questions" :question (get-question-as-map q)}))

(defn get-answer []
  (rules/ask-question (.getBeerStyle (.getBeer q)))
  (let [bs (db/get-beer-style-by-name (.getNameOfBeerStyle (.getBeerStyle (.getBeer q))))]
    (.setId (.getBeerStyle (.getBeer q)) (:beerstyleid bs)))
  )

(defn get-question-from-rules [answer]
  (.setAnswer q answer)
  (rules/ask-question q)
  (println "id: " (.getId (.getBeerStyle (.getBeer q))))
  (if (.isEnd q)
      (get-answer))
  )

;; (defn get-question-from-rules [answer]
;;   (.setAnswer q answer)
;;   (rules/askQuestion q))

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
