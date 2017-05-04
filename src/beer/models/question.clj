(ns beer.models.question
  (:require [compojure.core :refer :all]))

;; (defrecord  Question [text answer suggestedAnswers previousQuestion isEnd beer])

(defprotocol PQuestion
  (getText [this])
  (setText [this val])
  (getAnswer [this])
  (setAnswer [this val])
  (getSuggestedAnswers [this])
  (setSuggestedAnswers [this val])
  (getPreviousQuestion [this])
  (setPreviousAnswer [this val])
  (isEnd [this])
  (setEnd [this val])
  (getBeer [this])
  (setBeer [this val]))

(deftype Question [
                       ^:volatile-mutable text
                       ^:volatile-mutable answer
                       ^:volatile-mutable suggestedAnswers
                       ^:volatile-mutable previousQuestion
                       ^:volatile-mutable isEnd
                       ^:volatile-mutable beer
                       ]
                         PQuestion

  (toString [this]        (str text "->" answer " : " suggestedAnswers " : " previousQuestion " : " isEnd ", beer: " beer))
  (getText  [this]      text)
  (setText  [this val]  (set! text val))
  (getAnswer   [this]       answer)
  (setAnswer   [this val]  (set! answer val))
  (getSuggestedAnswers   [this]       suggestedAnswers)
  (setSuggestedAnswers   [this val]  (set! suggestedAnswers val))
  (getPreviousQuestion   [this]       previousQuestion)
  (setPreviousAnswer   [this val]  (set! previousQuestion val))
  (isEnd   [this]       isEnd)
  (setEnd   [this val]  (set! isEnd val))
  (getBeer   [this]       beer)
  (setBeer   [this val]  (set! beer val)))
