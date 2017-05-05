(ns beer.models.beerStyle
  (:require [compojure.core :refer :all]))

(defprotocol PBeerStyle
  (getId [this])
  (setId [this val])
  (getNameOfBeerStyle [this])
  (setNameOfBeerStyle [this val])
  (getDescription [this])
  (setDescription [this val])
  (getPicture [this])
  (setPicture [this val])
  (getTypeOfBeerStyle [this])
  (setTypeOfBeerStyle [this val])
  (getStrength [this])
  (setStrength [this val])
  (getColor [this])
  (setColor [this val])
  (getTaste [this])
  (setTaste [this val])
  )

(deftype BeerStyle [
                ^:volatile-mutable id
                ^:volatile-mutable nameOfBeerStyle
                ^:volatile-mutable description
                ^:volatile-mutable picture
                ^:volatile-mutable typeOfBeerStyle
                ^:volatile-mutable strength
                ^:volatile-mutable color
                ^:volatile-mutable taste
                       ]
                         PBeerStyle

  (toString [this]        (str id ", " nameOfBeerStyle ", " description))
  (getId  [this]      id)
  (setId  [this val]  (set! id val))
  (getNameOfBeerStyle  [this]      nameOfBeerStyle)
  (setNameOfBeerStyle  [this val]  (set! nameOfBeerStyle val))
  (getDescription   [this]       description)
  (setDescription   [this val]  (set! description val))
  (getPicture [this]       picture)
  (setPicture   [this val]  (set! picture val))
  (getTypeOfBeerStyle   [this] typeOfBeerStyle)
  (setTypeOfBeerStyle   [this val]  (set! typeOfBeerStyle val))
  (getStrength   [this] strength)
  (setStrength   [this val]  (set! strength val))
  (getColor [this] color)
  (setColor [this val] (set! color val))
  (getTaste [this] taste)
  (setTaste [this val] (set! taste val))
  )
