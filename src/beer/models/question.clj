(ns beer.models.question
  (:require [compojure.core :refer :all]))

(defprotocol PQuestion
  (getText [this])
  (setText [this val])
  (getAnswer [this])
  (setAnswer [this val])
  (getSuggestedAnswers [this])
  (setSuggestedAnswers [this val])
  (getPreviousQuestion [this])
  (setPreviousQuestion [this val])
  (isEnd [this])
  (setEnd [this val])
  (getIdBs [this])
  (setIdBs [this val])
  (getNameBs [this])
  (setNameBs [this val])
  (getTypeBs [this])
  (getLocation [this])
  (setLocation [this val])
  (setTypeBs [this val])
  (getStrength [this])
  (setStrength [this val])
  (getColor [this])
  (setColor [this val])
  (getTaste [this])
  (setTaste [this val]))

(deftype Question
  [^:volatile-mutable text
   ^:volatile-mutable answer
   ^:volatile-mutable suggestedAnswers
   ^:volatile-mutable previousQuestion
   ^:volatile-mutable isEnd
   ^:volatile-mutable idBs
   ^:volatile-mutable nameBs
   ^:volatile-mutable typeBs
   ^:volatile-mutable location
   ^:volatile-mutable strength
   ^:volatile-mutable color
   ^:volatile-mutable taste]
  PQuestion

  (toString [this] (str text "->" answer " : " suggestedAnswers " : " previousQuestion " : " isEnd))
  (getText  [this] text)
  (setText  [this val]  (set! text val))
  (getAnswer   [this]       answer)
  (setAnswer   [this val]  (set! answer val))
  (getSuggestedAnswers   [this]       suggestedAnswers)
  (setSuggestedAnswers   [this val]  (set! suggestedAnswers val))
  (getPreviousQuestion   [this]       previousQuestion)
  (setPreviousQuestion   [this val]  (set! previousQuestion val))
  (isEnd   [this] isEnd)
  (setEnd   [this val]  (set! isEnd val))
  (getIdBs  [this] idBs)
  (setIdBs [this val] (set! idBs val))
  (getNameBs  [this] nameBs)
  (setNameBs [this val] (set! nameBs val))
  (getTypeBs   [this] typeBs)
  (setTypeBs [this val]  (set! typeBs val))
  (getLocation  [this] location)
  (setLocation  [this val]  (set! location val))
  (getStrength   [this] strength)
  (setStrength   [this val]  (set! strength val))
  (getColor [this] color)
  (setColor [this val] (set! color val))
  (getTaste [this] taste)
  (setTaste [this val] (set! taste val)))
