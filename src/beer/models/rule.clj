(ns beer.models.rule
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [clara.rules :refer :all])
  (:import [beer.models.question Question]))

(defrule location
  [?q <- Question (= nil text)]
  =>
  (.setText ?q "Where would you rather drink beer?")
  (.setSuggestedAnswers ?q ["At home" "In front of a building" "In a pub" "In a club"])
  (println "location"))

(defrule location-building
  [?q <- Question (= "Where would you rather drink beer?" text) (= "In front of a building" answer)]
  =>
  (.setText ?q "Would you rather drink light or dark beer?")
  (.setSuggestedAnswers ?q ["Light" "Dark"])
  (println "location-building"))

(defrule location-other
  [?q <- Question (= "Where would you rather drink beer?" text) (not= "In front of a building" answer)]
  =>
  (.setLocation ?q (.getAnswer ?q))
  (.setText ?q "Do you prefer more classic or specific tastes?")
  (.setSuggestedAnswers ?q ["Classic" "Specific"])
  (println "location-other"))

(defrule type-lager
  [?q <- Question (= "Do you prefer more classic or specific tastes?" text) (= "Classic" answer)]
  =>
  (.setTypeBs ?q "Lager")
  (.setText ?q "Would you rather drink light or dark beer?")
  (.setSuggestedAnswers ?q ["Light" "Dark"])
  (println "type-lager"))

(defrule type-ale
  [?q <- Question (= "Do you prefer more classic or specific tastes?" text) (= "Specific" answer)]
  =>
  (.setTypeBs ?q "Ale")
  (.setText ?q "Do you prefer low or strong alcohol drinks?")
  (.setSuggestedAnswers ?q ["Low" "Strong"])
  (println "type-ale"))

(defrule lager-light
  [?q <- Question (= "Would you rather drink light or dark beer?" text) (= "Light" answer) (not= "In front of a building" location)]
  =>
  (.setColor ?q "Light")
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "Drinkable"])
  (println "lager-light"))

(defrule lager-light-location-building
  [?q <- Question (= "Would you rather drink light or dark beer?" text) (= "Light" answer) (= "In front of a building" location)]
  =>
  (.setColor ?q "Light")
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Drinkable"])
  (println "lager-light-location-building"))

(defrule lager-dark
  [?q <- Question (= "Would you rather drink light or dark beer?" text) (= "Dark" answer) (not= "In front of a building" location)]
  =>
  (.setColor ?q "Dark")
  (.setText ?q "Do you prefer low or strong alcohol drinks?")
  (.setSuggestedAnswers ?q ["Low" "Strong"])
  (println "lager-dark"))

(defrule lager-dark-location-building
  [?q <- Question (= "Would you rather drink light or dark beer?" text) (= "Dark" answer) (= "In front of a building" location)]
  =>
  (.setColor ?q "Dark")
  (.setStrength ?q "Strong")
  (.setEnd ?q true)
  (println "lager-dark-location-building"))

(defrule lager-light-taste
  [?q <- Question (= "You most like your beer?" text) (not= nil answer) (= "Lager" typeBs) (not= "In front of a building" location)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "lager-light-taste"))

(defrule lager-dark-strength
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" typeBs) (not= "In front of a building" location)]
  =>
  (.setStrength ?q (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "lager-dark-strength"))

(defrule lager-light-location-building-end
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" typeBs) (not= "In front of a building" location)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setEnd ?q true)
  (println "lager-light-location-building-end"))

(defrule lager-dark-strength-location-building-end
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" typeBs) (not= "In front of a building" location)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setEnd ?q true)
  (println "lager-dark-strength-location-building-end"))

(defrule ale-strong
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (= "Strong" answer) (= "Ale" typeBs)]
  =>
  (.setStrength ?q (.getAnswer ?q))
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "With full taste"])
  (println "ale-strong"))

(defrule ale-low
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (= "Low" answer) (= "Ale" typeBs)]
  =>
  (.setStrength ?q (.getAnswer ?q))
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "Drinkable" "Full taste"])
  (println "ale-low"))

(defrule domestic-imported-Bock-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Lager" typeBs) (= "Strong" strength)]
  =>
  (.setDomestic ?q (.getAnswer ?q))
  (.setPriceRange ?q "yes")
  (.setEnd ?q true)
  (println "domestic-imported-Bock-end"))

(defrule domestic-imported-Kellerbier-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Lager" typeBs) (= "Sweet" taste)]
  =>
  (.setDomestic (.getBeer ?q) (.getAnswer ?q))
  (.setPriceRange (.getBeer ?q) "yes")
  (.setEnd ?q true)
  (println "domestic-imported-Kellerbier-end"))

(defrule ale-low-full-taste
  [?q <- Question (= "You most like your beer?" text) (= "With full taste" answer) (= "Ale" typeBs) (= "Low" strength)]
  =>
  (.setTaste (.getBeer ?q) (.getAnswer ?q))
  (.setText ?q "Would you rather drink light, dark or Hazed pivo?")
  (.setSuggestedAnswers ?q ["Light" "Dark" "Hazed"])
  (println "ale-low-full-taste"))

(defrule ale-low-not-full-taste
  [?q <- Question (= "You most like your beer?" text) (not= "With full taste" answer) (= "Ale" typeBs) (= "Low" strength)]
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
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Ale" typeBs) (= "Low" strength) (not= nil color) (= "With full taste" taste)]
  =>
  (.setDomestic (.getBeer ?q) (.getAnswer ?q))
  (.setPriceRange (.getBeer ?q) "yes")
  (.setEnd ?q true)
  (println "ale-low-full-taste-color-end"))

(defrule ale-strong-sweet-bitter-end
  [?q <- Question (= "You most like your beer?" text) (not= "With full taste" answer) (= "Ale" typeBs) (= "Strong" strength)]
  =>
  (.setTaste (.getBeer ?q) (.getAnswer ?q))
  (.setDomestic (.getBeer ?q) "Imported")
  (.setPriceRange (.getBeer ?q) "yes")
  (.setEnd ?q true)
  (println "ale-strong-sweet-bitter-end"))

(defrule ale-strong-full-taste
  [?q <- Question (= "You most like your beer?" text) (= "With full taste" answer) (= "Ale" typeBs) (= "Strong" strength)]
  =>
  (.setTaste (.getBeer ?q) (.getAnswer ?q))
  (.setPriceRange (.getBeer ?q) "yes")
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "ale-strong-full-taste"))

(defrule ale-strong-full-taste-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Ale" typeBs) (= "Strong" strength) (= "With full taste" taste)]
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
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Low" strength) (= "Sweet" taste)]
  =>
  (.setNameOfBeerStyle ?b "Belgian Pale Ale")
  (println "bs-ale-low-sweet"))

(defrule bs-ale-low-bitter
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Low" strength) (= "Bitter" taste)]
  =>
  (.setNameOfBeerStyle ?b "Indian Pale Ale")
  (println "bs-ale-low-bitter"))

(defrule bs-ale-low-drinkable
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Low" strength) (= "Drinkable" taste)]
  =>
  (.setNameOfBeerStyle ?b "Witbier")
  (println "bs-ale-low-drinkable"))

(defrule bs-ale-low-full-taste-light
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Low" strength) (= "With full taste" taste) (= "Light" color)]
  =>
  (.setNameOfBeerStyle ?b "Kristalweizen")
  (println "bs-ale-low-full-taste-light"))

(defrule bs-ale-low-full-taste-hazed
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Low" strength) (= "With full taste" taste) (= "Hazed" color)]
  =>
  (.setNameOfBeerStyle ?b "Heffeweizen")
  (println "bs-ale-low-full-taste-hazed"))

(defrule bs-ale-low-full-taste-dark
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Low" strength) (= "With full taste" taste) (= "Dark" color)]
  =>
  (.setNameOfBeerStyle ?b "Dunkelweizen")
  (println "bs-ale-low-full-taste-dark"))

(defrule bs-ale-strong-sweet
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Strong" strength) (= "Sweet" taste)]
  =>
  (.setNameOfBeerStyle ?b "Belgian Strong Ale")
  (println "bs-ale-strong-sweet"))

(defrule bs-ale-strong-full-taste
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Strong" strength) (= "With full taste" taste)]
  =>
  (.setNameOfBeerStyle ?b "Stout/Porter")
  (println "bs-ale-strong-full-taste"))

(defrule bs-ale-strong-bitter
  [?b <- Question (= true isEnd) (= "Ale" typeBs) (= "Strong" strength) (= "Bitter" taste)]
  =>
  (.setNameOfBeerStyle ?b "Scocth Ale")
  (println "bs-ale-strong-bitter"))

(defrule bs-lager-light-bitter
  [?b <- Question (= true isEnd) (= "Lager" typeBs) (= "Light" color) (= "Bitter" taste)]
  =>
  (.setNameOfBeerStyle ?b "Pilsner")
  (println "bs-lager-light-bitter"))

(defrule bs-lager-light-sweet
  [?b <- Question (= true isEnd) (= "Lager" typeBs) (= "Light" color) (= "Sweet" taste)]
  =>
  (.setNameOfBeerStyle ?b "Kellerbier")
  (println "bs-lager-light-sweet"))

(defrule bs-lager-light-drinkable
  [?b <- Question (= true isEnd) (= "Lager" typeBs) (= "Light" color) (= "Drinkable" taste)]
  =>
  (.setNameOfBeerStyle ?b "Pale Lager")
  (println "bs-lager-light-drinkable"))

(defrule bs-lager-dark-low
  [?b <- Question (= true isEnd) (= "Lager" typeBs) (= "Dark" color) (= "Low" strength)]
  =>
  (.setNameOfBeerStyle ?b "Dark Lager")
  (println "bs-lager-dark-low"))

(defrule bs-lager-dark-strong
  [?b <- Question (= true isEnd) (= "Lager" typeBs) (= "Dark" color) (= "Strong" strength)]
  =>
  (.setNameOfBeerStyle ?b "Bock")
  (println "bs-lager-dark-strong"))


(defn ask-question [q]
  (-> (mk-session 'beer.models.rule)
    (insert
      q)
    (fire-rules)))

