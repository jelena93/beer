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

(defn authenticated [session]
  (authenticated? session))

(defn get-question-as-map [q]
  {:text (.getText q) :suggestedAnswers (.getSuggestedAnswers q) :id (.getIdBs q) :origin (.getOrigin q) :price (.getPrice q)})

(defn get-question-page [{:keys [params session] request :request}]
   (if-not (authenticated session)
    (redirect "/login")
     (do (def q (->Question nil nil nil nil nil nil 0 0 nil nil nil nil))
       (rules/ask-question q)
       (render-file "templates/question.html" {:title "Questions" :logged (:identity session) :question (get-question-as-map q)}))))

(defn get-question-from-rules [answer]
  (.setAnswer q answer)
  (rules/ask-question q)
  (println q)
(if-not (nil? (.getNameBs q))
  (let [bs (first (db/find-beer-style-by-name (.getNameBs q)))]
    (.setIdBs q  (:id bs)))))

(defn find-bs-result [{:keys [params session] request :request}]
  (cond
    (not (authenticated session))
     (redirect "/login")
    :else
    (render-file "templates/bs-user.html" {:title "Beer Style" :logged (:identity session)
                                        :bs (first (db/find-beer-style-by-id (:bs params)))
                                    :beers (db/find-beer-by-beer-style-origin-price (:bs params) (:origin params) (:price params))})))


(defresource get-question [{:keys [params session] request :request}]
  :allowed-methods [:post]
  :handle-malformed "answer cannot be empty"
  :authenticated? (authenticated session)
  :new? false
  :respond-with-entity? true
  :post!  (get-question-from-rules (:answer params))
  :handle-ok (fn [_] (json/write-str (get-question-as-map q)))
  :available-media-types ["application/json"])

(defroutes question-routes
  (GET "/questions" request (get-question-page request))
  (POST "/questions" request (get-question request))
  (GET "/result" request (find-bs-result request)))
