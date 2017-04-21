(ns beer.models.rule
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [clara.rules :refer :all])
  (:import [beer.models.question Question]))

(defrecord SupportRequest [client level])

(defrecord ClientRepresentative [name client])


(defrule location
  "Find important support requests."
  [?q <- Question (= nil text)]
  =>
  ;(insert! (Question. "test" 42))
  (.setText ?q "teeext")
  (insert! (Question. "test" 42  [1 2] nil false))
  (println "High support requested!" ?q))

(defrule asddd
  "Find important support requests."
  [Question (= "test" text) (= 42 answer)]
  =>
  (println "pogodio!"))

(defrule notify-client-rep
  [SupportRequest (= ?client client)]
  [ClientRepresentative (= ?client client) (= ?name name)]
  =>
  (println "Notify" ?name "that"
          ?client "has a new support request!"))

(defn asd [q]
  (-> (mk-session 'beer.models.rule)
    (insert
      q)
    (fire-rules)))
