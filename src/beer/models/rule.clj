(ns beer.models.rule
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [beer.models.beer :refer :all]
            [beer.models.beerStyle :refer :all]
            [clara.rules :refer :all])
  (:import [beer.models.question Question]
           [beer.models.beer Beer]
           [beer.models.beerStyle BeerStyle]
           ))
;;beer -> location priceRange domestic nameOfBeer alcohol manufacturer country info beerStyle
;;beerStyle -> nameOfBeerStyle description picture typeOfBeerStyle strength color taste

(defrule location
  [?q <- Question (= nil text)]
  =>
  (.setText ?q "Where would you rather drink beer?")
  (.setSuggestedAnswers ?q ["At home" "In front of a building" "In a pub" "In a club"])
  (println "location"))

(defrule location-building
  [?q <- Question (= "Where would you rather drink beer?" text) (= "In front of a building" answer)]
  =>
  (.setBeer ?q (Beer. (.getAnswer ?q) "no" nil nil nil nil nil nil (BeerStyle. nil nil nil "Lager" nil nil nil)))
  (.setText ?q "Would you rather drink light or dark beer?")
  (.setSuggestedAnswers ?q ["Light" "Dark"])
  (println "location-building"))

(defrule location-other
  [?q <- Question (= "Where would you rather drink beer?" text) (not= "In front of a building" answer)]
  =>
  (.setLocation (.getBeer ?q) (.getAnswer ?q))
  (.setText ?q "Do you prefer more classic or specific tastes?")
  (.setSuggestedAnswers ?q ["Classic" "Specific"])
  (println "location-other"))

(defrule type-lager
  [?q <- Question (= "Do you prefer more classic or specific tastes?" text) (= "Classic" answer)]
  =>
  (.setTypeOfBeerStyle (.getBeerStyle (.getBeer ?q)) "Lager")
  (.setText ?q "Would you rather drink light or dark beer?")
  (.setSuggestedAnswers ?q ["Light" "Dark"])
  (println "type-lager"))

(defrule type-ale
  [?q <- Question (= "Do you prefer more classic or specific tastes?" text) (= "Specific" answer)]
  =>
  (.setTypeOfBeerStyle (.getBeerStyle (.getBeer ?q)) "Ale")
  (.setText ?q "Do you prefer low or strong alcohol drinks?")
  (.setSuggestedAnswers ?q ["Low" "Strong"])
  (println "type-ale"))

(defrule lager-light
  [?q <- Question (= "Would you rather drink light or dark beer?" text) (= "Light" answer) (not= "In front of a building" (.getLocation beer))]
  =>
  (.setColor (.getBeerStyle (.getBeer ?q)) "Light")
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "Drinkable"])
  (println "lager-light"))

(defrule lager-light-location-building
  [?q <- Question (= "Would you rather drink light or dark beer?" text) (= "Light" answer) (= "In front of a building" (.getLocation beer))]
  =>
  (.setColor (.getBeerStyle (.getBeer ?q)) "Light")
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Drinkable"])
  (println "lager-light-location-building"))

(defrule lager-dark
  [?q <- Question (= "Would you rather drink light or dark beer?" text) (= "Dark" answer) (not= "In front of a building" (.getLocation beer))]
  =>
  (.setColor (.getBeerStyle (.getBeer ?q)) "Dark")
  (.setText ?q "Do you prefer low or strong alcohol drinks?")
  (.setSuggestedAnswers ?q ["Low" "Strong"])
  (println "lager-dark"))

(defrule lager-dark-location-building
  [?q <- Question (= "Would you rather drink light or dark beer?" text) (= "Dark" answer) (= "In front of a building" (.getLocation beer))]
  =>
  (.setColor (.getBeerStyle (.getBeer ?q)) "Dark")
  (.setStrength (.getBeerStyle (.getBeer ?q)) "Strong")
  (.setEnd ?q true)
  (println "lager-dark-location-building"))

(defrule lager-light-taste
  [?q <- Question (= "You most like your beer?" text) (not= nil answer) (= "Lager" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (not= "In front of a building" (.getLocation beer))]
  =>
  (.setTaste (.getBeerStyle (.getBeer ?q)) (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "lager-light-taste"))

(defrule lager-dark-strength
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (not= "In front of a building" (.getLocation beer))]
  =>
  (.setStrength (.getBeerStyle (.getBeer ?q)) (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "lager-dark-strength"))

(defrule lager-light-location-building-end
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (not= "In front of a building" (.getLocation beer))]
  =>
  (.setTaste (.getBeerStyle (.getBeer ?q)) (.getAnswer ?q))
  (.setEnd ?q true)
  (println "lager-light-location-building-end"))

(defrule lager-dark-strength-location-building-end
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (not= "In front of a building" (.getLocation beer))]
  =>
  (.setTaste (.getBeerStyle (.getBeer ?q)) (.getAnswer ?q))
  (.setEnd ?q true)
  (println "lager-dark-strength-location-building-end"))

(defrule ale-strong
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (= "Strong" answer) (= "Ale" (.getTypeOfBeerStyle (.getBeerStyle beer)))]
  =>
  (.setStrength (.getBeerStyle (.getBeer ?q)) (.getAnswer ?q))
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "With full taste"])
  (println "ale-strong"))

(defrule ale-low
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (= "Low" answer) (= "Ale" (.getTypeOfBeerStyle (.getBeerStyle beer)))]
  =>
  (.setStrength (.getBeerStyle (.getBeer ?q)) (.getAnswer ?q))
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "Drinkable" "Full taste"])
  (println "ale-low"))

(defrule domestic-imported-Bock-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Lager" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (= "Strong" (.getStrength (.getBeerStyle beer)))]
  =>
  (.setDomestic (.getBeer ?q) (.getAnswer ?q))
  (.setPriceRange (.getBeer ?q) "yes")
  (.setEnd ?q true)
  (println "domestic-imported-Bock-end"))

(defrule domestic-imported-Kellerbier-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Lager" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (= "Sweet" (.getTaste (.getBeerStyle beer)))]
  =>
  (.setDomestic (.getBeer ?q) (.getAnswer ?q))
  (.setPriceRange (.getBeer ?q) "yes")
  (.setEnd ?q true)
  (println "domestic-imported-Kellerbier-end"))

(defrule ale-low-full-taste
  [?q <- Question (= "You most like your beer?" text) (= "With full taste" answer) (= "Ale" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (= "Low" (.getStrength (.getBeerStyle beer)))]
  =>
  (.setTaste (.getBeer ?q) (.getAnswer ?q))
  (.setText ?q "Would you rather drink light, dark or Hazed pivo?")
  (.setSuggestedAnswers ?q ["Light" "Dark" "Hazed"])
  (println "ale-low-full-taste"))

(defrule ale-low-not-full-taste
  [?q <- Question (= "You most like your beer?" text) (not= "With full taste" answer) (= "Ale" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (= "Low" (.getStrength (.getBeerStyle beer)))]
  =>
  (.setTaste (.getBeer ?q) (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "ale-low-not-full-taste"))

(defrule ale-low-full-taste-color
  [?q <- Question (= "Would you rather drink light, dark or Hazed pivo?" text) (not= nil answer)]
  =>
  (.setColor (.getBeer ?q) (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "ale-low-full-taste-color"))

(defrule ale-low-full-taste-color-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Ale" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (= "Low" (.getStrength (.getBeerStyle beer))) (not= nil (.getColor (.getBeerStyle beer))) (= "With full taste" (.getTaste (.getBeerStyle beer)))]
  =>
  (.setDomestic (.getBeer ?q) (.getAnswer ?q))
  (.setPriceRange (.getBeer ?q) "yes")
  (.setEnd ?q true)
  (println "ale-low-full-taste-color-end"))

(defrule ale-strong-sweet-bitter-end
  [?q <- Question (= "You most like your beer?" text) (not= "With full taste" answer) (= "Ale" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (= "Strong" (.getStrength (.getBeerStyle beer)))]
  =>
  (.setTaste (.getBeer ?q) (.getAnswer ?q))
  (.setDomestic (.getBeer ?q) "Imported")
  (.setPriceRange (.getBeer ?q) "yes")
  (.setEnd ?q true)
  (println "ale-strong-sweet-bitter-end"))

(defrule ale-strong-full-taste
  [?q <- Question (= "You most like your beer?" text) (= "With full taste" answer) (= "Ale" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (= "Strong" (.getStrength (.getBeerStyle beer)))]
  =>
  (.setTaste (.getBeer ?q) (.getAnswer ?q))
  (.setPriceRange (.getBeer ?q) "yes")
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "ale-strong-full-taste"))

(defrule ale-strong-full-taste-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Ale" (.getTypeOfBeerStyle (.getBeerStyle beer)))
   (= "Strong" (.getStrength (.getBeerStyle beer))) (= "With full taste" (.getTaste (.getBeerStyle beer))) ]
  =>
  (.setDomestic (.getBeer ?q) (.getAnswer ?q))
  (.setEnd ?q true)
  (println "ale-strong-full-taste-end"))

(defrule domestic-imported
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer)]
  =>
  (.setDomestic (.getBeer ?q) (.getAnswer ?q))
  (.setText ?q "Would you spend more money on beer?")
  (.setSuggestedAnswers ?q ["Yes" "No"])
  (println "domestic-imported"))

(defrule domestic-imported
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer)]
  =>
  (.setDomestic (.getBeer ?q) (.getAnswer ?q))
  (.setText ?q "Would you spend more money on a good beer?")
  (.setSuggestedAnswers ?q ["Yes" "No"])
  (println "domestic-imported"))

(defrule expensive-cheap
  [?q <- Question (= "Would you spend more money on a good beer?" text) (not= nil answer)]
  =>
  (.setPriceRange (.getBeer ?q) (.getAnswer ?q))
  (.setEnd ?q true)
  (println "expensive-cheap"))

; -------------------------------------suggesting-beer------------------------------

(defrule bs-ale-low-sweet
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Low" strength) (= "Sweet" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Belgian Pale Ale")
  (println "bs-ale-low-sweet"))

(defrule bs-ale-low-bitter
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Low" strength) (= "Bitter" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Indian Pale Ale")
  (println "bs-ale-low-bitter"))

(defrule bs-ale-low-drinkable
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Low" strength) (= "Drinkable" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Witbier")
  (println "bs-ale-low-drinkable"))

(defrule bs-ale-low-full-taste-light
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Low" strength) (= "With full taste" taste) (= "Light" color)]
  =>
  (.setNameOfBeerStyle ?bs "Kristalweizen")
  (println "bs-ale-low-full-taste-light"))

(defrule bs-ale-low-full-taste-hazed
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Low" strength) (= "With full taste" taste) (= "Hazed" color)]
  =>
  (.setNameOfBeerStyle ?bs "Heffeweizen")
  (println "bs-ale-low-full-taste-hazed"))

(defrule bs-ale-low-full-taste-dark
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Low" strength) (= "With full taste" taste) (= "Dark" color)]
  =>
  (.setNameOfBeerStyle ?bs "Dunkelweizen")
  (println "bs-ale-low-full-taste-dark"))

(defrule bs-ale-strong-sweet
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Strong" strength) (= "Sweet" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Belgian Strong Ale")
  (println "bs-ale-strong-sweet"))

(defrule bs-ale-strong-full-taste
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Strong" strength) (= "With full taste" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Stout/Porter")
  (println "bs-ale-strong-full-taste"))

(defrule bs-ale-strong-bitter
  [?bs <- BeerStyle (= "Ale" typeOfBeer) (= "Strong" strength) (= "Bitter" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Scocth Ale")
  (println "bs-ale-strong-bitter"))

(defrule bs-lager-light-bitter
  [?bs <- BeerStyle (= "Lager" typeOfBeer) (= "Light" color) (= "Bitter" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Pilsner")
  (println "bs-lager-light-bitter"))

(defrule bs-lager-light-sweet
  [?bs <- BeerStyle (= "Lager" typeOfBeer) (= "Light" color) (= "Sweet" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Kellerbier")
  (println "bs-lager-light-sweet"))

(defrule bs-lager-light-drinkable
  [?bs <- BeerStyle (= "Lager" typeOfBeer) (= "Light" color) (= "Drinkable" taste)]
  =>
  (.setNameOfBeerStyle ?bs "Pale Lager")
  (println "bs-lager-light-drinkable"))

(defrule bs-lager-dark-low
  [?bs <- BeerStyle (= "Lager" typeOfBeer) (= "Dark" color) (= "Low" strength)]
  =>
  (.setNameOfBeerStyle ?bs "Dark Lager")
  (println "bs-lager-dark-low"))

(defrule bs-lager-dark-strong
  [?bs <- BeerStyle (= "Lager" typeOfBeer) (= "Dark" color) (= "Strong" strength)]
  =>
  (.setNameOfBeerStyle ?bs "Bock")
  (println "bs-lager-dark-strong"))


(defn askQuestion [q]
  (-> (mk-session 'beer.models.rule)
    (insert
      q)
    (fire-rules)))
