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

(defn question->map [q]
  (.setAnswer q nil)
  (if-not (nil? (.getStyleName q))
    (.setStyleId q (->(.getStyleName q)
                      (hash-map :name)
                      (db/find-style)
                      (first)
                      (:id))))
  (println "posle: " q)
  {:text (.getText q)
   :answer (.getAnswer q)
   :suggestedAnswers (.getSuggestedAnswers q)
   :id (.getStyleId q)
   :style (.getStyleName q)
   :type (.getStyleType q)
   :price (.getPrice q)
   :origin (.getOrigin q)
   :location (.getLocation q)
   :strength (.getStrength q)
   :color (.getColor q)
   :taste (.getTaste q)})

(defn map->question [{:keys [question]}]
  (->Question (:text question)
              (:answer question)
              (:suggestedAnswers nil)
              (:id question)
              (:style question)
              (:type question)
              (:price question)
              (:origin question)
              (:location question)
              (:strength question)
              (:color question)
              (:taste question)))

(defn get-question-page [{:keys [params session]}]
  (if-not (authenticated? session)
    (redirect "/login")
    (render-file "templates/question.html" {:title "Questions"
                                            :logged (:identity session)})))

(defn get-question-from-rules [params]
    (-> (map->question params)
        (rules/ask-question)
        (first)
        (:?q)
        (question->map)))

(defn find-style-result [{:keys [params session]}]
  (if-not (authenticated? session)
    (redirect "/login")
    (render-file "templates/style-user.html" {:title "Style"
                                              :logged (:identity session)
                                              :style (first (db/find-style (select-keys params [:id])))
                                              :beers (db/find-beer (select-keys params [:id :origin :price]))})))

(defresource get-question [{:keys [params session]}]
  :allowed-methods [:post]
  :authorized? (fn [_] (authenticated? session))
  :new? false
  :respond-with-entity? true
  :handle-ok (fn [_] (json/write-str (get-question-from-rules params)))
  :available-media-types ["application/json"])

(defroutes question-routes
  (GET "/questions" request (get-question-page request))
  (POST "/questions" request (get-question request))
  (GET "/result" request (find-style-result request)))
