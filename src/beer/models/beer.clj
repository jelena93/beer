(ns beer.models.beer
  (:require [compojure.core :refer :all]))

(defprotocol PBeer
  (getLocation [this])
  (setLocation [this val])
  (getPriceRange [this])
  (setPriceRange [this val])
  (getDomestic [this])
  (setDomestic [this val])
  (getNameOfBeer [this])
  (setNameOfBeer [this val])
  (getAlcohol [this])
  (setAlcohol [this val])
  (getManufacturer [this])
  (setManufacturer [this val])
  (getCountry [this])
  (setCountry [this val])
  (getInfo [this])
  (setInfo [this val])
  (getBeerStyle [this])
  (setBeerStyle [this val])
  )

(deftype Beer [
                ^:volatile-mutable location
                ^:volatile-mutable priceRange
                ^:volatile-mutable domestic
                ^:volatile-mutable nameOfBeer
                ^:volatile-mutable alcohol
                ^:volatile-mutable manufacturer
                ^:volatile-mutable country
                ^:volatile-mutable info
                ^:volatile-mutable beerStyle
                       ]
                         PBeer

  (toString [this]        (str location ", " priceRange))
  (getLocation  [this]      location)
  (setLocation  [this val]  (set! location val))
  (getPriceRange   [this]       priceRange)
  (setPriceRange   [this val]  (set! priceRange val))
  (getDomestic   [this]       domestic)
  (setDomestic   [this val]  (set! domestic val))
  (getNameOfBeer   [this] nameOfBeer)
  (setNameOfBeer   [this val]  (set! nameOfBeer val))
  (getAlcohol   [this] alcohol)
  (setAlcohol   [this val]  (set! alcohol val))
  (getManufacturer [this] manufacturer)
  (setManufacturer [this val] (set! manufacturer val))
  (getCountry [this] country)
  (setCountry [this val] (set! country val))
  (getInfo  [this]       info)
  (setInfo   [this val]  (set! info val))
  (getBeerStyle  [this] beerStyle)
  (setBeerStyle   [this val]  (set! beerStyle val))
  )
