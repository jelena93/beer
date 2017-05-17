(ns beer.models.question
  (:require [compojure.core :refer :all]))
;; origin-domestic 1 domace
;; origin-imported 0 strano
;; price-cheap 1 jeftino
;; price-expensive 0 skupo ne

(defprotocol PQuestion
  (getText [this])
  (setText [this val])
  (getAnswer [this])
  (setAnswer [this val])
  (getSuggestedAnswers [this])
  (setSuggestedAnswers [this val])
  (getIdBs [this])
  (setIdBs [this val])
  (getNameBs [this])
  (setNameBs [this val])
  (getTypeBs [this])
  (setTypeBs [this val])
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
   ^:volatile-mutable idBs
   ^:volatile-mutable nameBs
   ^:volatile-mutable typeBs
   ^:volatile-mutable price
   ^:volatile-mutable origin
   ^:volatile-mutable location
   ^:volatile-mutable strength
   ^:volatile-mutable color
   ^:volatile-mutable taste]
  PQuestion

;;   (toString [this] (str "text:" text " answer:" answer " suggestedAnswers: " suggestedAnswers " end " isEnd " idBs: " idBs " nameBs: " nameBs))
  (toString [this] (str "id: " idBs " name: " nameBs " location: " location " type: " typeBs " origin: " origin " price: " price " taste: " taste " color: " color))
  (getText  [this] text)
  (setText  [this val]  (set! text val))
  (getAnswer   [this]       answer)
  (setAnswer   [this val]  (set! answer val))
  (getSuggestedAnswers   [this]       suggestedAnswers)
  (setSuggestedAnswers   [this val]  (set! suggestedAnswers val))
  (getIdBs  [this] idBs)
  (setIdBs [this val] (set! idBs val))
  (getNameBs  [this] nameBs)
  (setNameBs [this val] (set! nameBs val))
  (getTypeBs   [this] typeBs)
  (setTypeBs [this val]  (set! typeBs val))
  (getPrice   [this] price)
  (setPrice [this val]  (set! price val))
  (getOrigin [this] origin)
  (setOrigin [this val] (set! origin val))
  (getLocation  [this] location)
  (setLocation  [this val]  (set! location val))
  (getStrength   [this] strength)
  (setStrength   [this val]  (set! strength val))
  (getColor [this] color)
  (setColor [this val] (set! color val))
  (getTaste [this] taste)
  (setTaste [this val] (set! taste val)))
