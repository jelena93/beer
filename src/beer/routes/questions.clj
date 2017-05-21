(ns beer.routes.questions
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [beer.models.db :as db]
            [beer.models.rule :as rules]
            [selmer.parser :refer [render-file]]
            [compojure.response :refer [render]]
            [ring.util.response :refer [response redirect content-type]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [buddy.auth :refer [authenticated?]])
    (:import [beer.models.question Question]))

(defn get-question-as-map [q]
  {:text (.getText q)
   :suggestedAnswers (.getSuggestedAnswers q)
   :id (.getStyleId q)
   :origin (.getOrigin q)
   :price (.getPrice q)})

(defn get-question-page [{:keys [params session]}]
   (if-not (authenticated? session)
    (redirect "/login")
     (do (def q (->Question nil nil nil nil nil nil 0 0 nil nil nil nil))
       (rules/ask-question q)
       (render-file "templates/question.html" {:title "Questions"
                                               :logged (:identity session)
                                               :question (get-question-as-map q)}))))

(defn get-question-from-rules [answer]
  (.setAnswer q answer)
  (rules/ask-question q)
(if-not (nil? (.getStyleName q))
  (let [style (first (db/find-style-by-name (.getStyleName q)))]
    (.setStyleId q (:id style)))))

(defn find-style-result [{:keys [params session]}]
  (cond
    (not (authenticated? session))
     (redirect "/login")
    :else
    (render-file "templates/style-user.html" {:title "Style"
                                           :logged (:identity session)
                                           :style (first (db/find-style-by-id (:style params)))
                                           :beers (db/find-beer-by-style-origin-price (:style params) (:origin params) (:price params))})))


(defresource get-question [{:keys [params session]}]
  :allowed-methods [:post]
  :handle-malformed "answer cannot be empty"
  :authorized? (fn [_] (authenticated? session))
  :new? false
  :respond-with-entity? true
  :post! (fn [_] (get-question-from-rules (:answer params)))
  :handle-ok (fn [_] (json/write-str (get-question-as-map q)))
  :available-media-types ["application/json"])

(defroutes question-routes
  (GET "/questions" request (get-question-page request))
  (POST "/questions" request (get-question request))
  (GET "/result" request (find-style-result request)))
