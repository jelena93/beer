(ns beer.rules.rules
  (:require [compojure.core :refer :all]
            [clara.rules :refer :all]
            [beer.models.db :as db]))

(defrecord SupportRequest [client level])

(defrecord ClientRepresentative [name client])


(defrule is-important
  "Find important support requests."
  [SupportRequest (= :high level)]
  =>
  (println "High support requested!"))

(defrule notify-client-rep
  "Find the client representative and request support."
  [SupportRequest (= ?client client)]
  [ClientRepresentative (= ?client client) (= ?name name)]
  =>
  (println "Notify" ?name "that"
          ?client "has a new support request!"))

(-> (mk-session 'clara.example)
    (insert (->ClientRepresentative "Alice" "Acme")
            (->SupportRequest "Acme" :high))
    (fire-rules))
