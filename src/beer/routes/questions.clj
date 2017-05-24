(ns beer.routes.questions
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [beer.models.db :as db]
            [beer.models.rule :as rules]
            [selmer.parser :refer [render-file]]
            [ring.util.response :refer [redirect]]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [buddy.auth :refer [authenticated?]])
  (:import [beer.models.question Question]))

;; (def q (->Question nil nil nil nil nil nil 0 0 nil nil nil nil))

(defn get-question-as-map [q]
  {:text (.getText q)
   :suggestedAnswers (.getSuggestedAnswers q)
   :id (.getStyleId q)
   :origin (.getOrigin q)
   :price (.getPrice q)})

(defn ask-question [params session]
  (render-file "templates/question.html" {:title "Questions"
                                          :logged (:identity session)
                                          :question (get-question-as-map (:?q (first (rules/ask-question (->Question nil nil nil nil nil nil 0 0 nil nil nil nil)))))}))

(defn get-question-page [{:keys [params session]}]
  (if-not (authenticated? session)
    (redirect "/login")
    (ask-question params session)))

(defn get-question-from-rules [{:keys [answer]}]
  (.setAnswer q answer)
  (rules/ask-question (:?q (first (rules/ask-question (->Question nil nil nil nil nil nil 0 0 nil nil nil nil)))))
  (if-not (nil? (.getStyleName q))
    (.setStyleId q (->(.getStyleName q)
                      (hash-map :name)
                      (db/find-style)
                      (first)
                      (:id)))))

(defn find-style-result [{:keys [params session]}]
  (if-not (authenticated? session)
    (redirect "/login")
    (render-file "templates/style-user.html" {:title "Style"
                                           :logged (:identity session)
                                           :style (first (db/find-style (select-keys params [:style])))
                                           :beers (db/find-beer (select-keys params [:style :origin :price]))})))


(defresource get-question [{:keys [params session]}]
  :allowed-methods [:post]
  :malformed? (fn [_] (empty? (:answer params)))
  :handle-malformed "Answer is required"
  :authorized? (fn [_] (authenticated? session))
  :new? false
  :respond-with-entity? true
  :handle-ok (fn [_] (json/write-str (get-question-from-rules params)))
  :available-media-types ["application/json"])

(defroutes question-routes
  (GET "/questions" request (get-question-page request))
  (POST "/questions" request (get-question request))
  (GET "/result" request (find-style-result request)))
