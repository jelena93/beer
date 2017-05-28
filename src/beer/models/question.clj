(ns beer.models.question
  (:require [compojure.core :refer :all]))

(defprotocol PQuestion
  (getText [this])
  (setText [this val])
  (getAnswer [this])
  (setAnswer [this val])
  (getSuggestedAnswers [this])
  (setSuggestedAnswers [this val])
  (getStyleId [this])
  (setStyleId [this val])
  (getStyleName [this])
  (setStyleName [this val])
  (getStyleType [this])
  (setStyleType [this val])
  (getPrice [this])
  (setPrice [this val])
  (getOrigin [this])
  (setOrigin [this val])
  (getLocation [this])
  (setLocation [this val])
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
   ^:volatile-mutable styleId
   ^:volatile-mutable styleName
   ^:volatile-mutable styleType
   ^:volatile-mutable price
   ^:volatile-mutable origin
   ^:volatile-mutable location
   ^:volatile-mutable strength
   ^:volatile-mutable color
   ^:volatile-mutable taste]
  PQuestion

  (toString [this] (str "text:" text ",answer:" answer ",suggested:" suggestedAnswers ",id:" styleId ",name:" styleName ",type:" styleType
      ",price:" price ",origin:" origin ",location:" location ",strength:" strength ",color:" color ",taste:" taste))
  (getText [this] text)
  (setText [this val] (set! text val))
  (getAnswer [this] answer)
  (setAnswer [this val] (set! answer val))
  (getSuggestedAnswers [this] suggestedAnswers)
  (setSuggestedAnswers [this val] (set! suggestedAnswers val))
  (getStyleId [this] styleId)
  (setStyleId [this val] (set! styleId val))
  (getStyleName [this] styleName)
  (setStyleName [this val] (set! styleName val))
  (getStyleType [this] styleType)
  (setStyleType [this val]  (set! styleType val))
  (getPrice [this] price)
  (setPrice [this val]  (set! price val))
  (getOrigin [this] origin)
  (setOrigin [this val] (set! origin val))
  (getLocation [this] location)
  (setLocation [this val] (set! location val))
  (getStrength [this] strength)
  (setStrength [this val] (set! strength val))
  (getColor [this] color)
  (setColor [this val] (set! color val))
  (getTaste [this] taste)
  (setTaste [this val] (set! taste val)))
