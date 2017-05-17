(ns beer.models.rule
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [clara.rules :refer :all])
  (:import [beer.models.question Question]))

(defrule location
  [?q <- Question (= nil text) (= nil answer)]
  =>
  (.setText ?q "Where would you rather drink beer?")
  (.setSuggestedAnswers ?q ["At home" "In front of a building" "In a pub" "In a club"])
  (println "location"))

(defrule location-building
  [?q <- Question (= "Where would you rather drink beer?" text) (= "In front of a building" answer)]
  =>
  (.setTypeBs ?q "Lager")
  (.setPrice ?q false)
  (.setOrigin ?q "Domestic")
  (.setLocation ?q (.getAnswer ?q))
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
  ;;(.setEnd ?q true)
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
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" typeBs) (= "In front of a building" location)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  ;;(.setEnd ?q true)
  (println "lager-light-location-building-end"))

(defrule lager-dark-strength-location-building-end
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" typeBs) (= "In front of a building" location)]
  =>
  (.setStrength ?q (.getAnswer ?q))
  ;;(.setEnd ?q true)
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
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "Drinkable" "With full taste"])
  (println "ale-low"))

(defrule domestic-imported-Bock-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Lager" typeBs) (= "Strong" strength)]
  =>
  (.setOrigin ?q (.getAnswer ?q))
  (.setPrice ?q true)
  ;;(.setEnd ?q true)
  (println "domestic-imported-Bock-end"))

(defrule domestic-imported-Kellerbier-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Lager" typeBs) (= "Sweet" taste)]
  =>
  (.setOrigin ?q (.getAnswer ?q))
  (.setPrice true)
  ;;(.setEnd ?q true)
  (println "domestic-imported-Kellerbier-end"))

(defrule ale-low-full-taste
  [?q <- Question (= "You most like your beer?" text) (= "With full taste" answer) (= "Ale" typeBs) (= "Low" strength)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setText ?q "Would you rather drink light, dark or hazed beer?")
  (.setSuggestedAnswers ?q ["Light" "Dark" "Hazed"])
  (println "ale-low-full-taste"))

(defrule ale-low-not-full-taste
  [?q <- Question (= "You most like your beer?" text) (not= "With full taste" answer) (= "Ale" typeBs) (= "Low" strength)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "ale-low-not-full-taste"))

(defrule ale-low-full-taste-color
  [?q <- Question (= "Would you rather drink light, dark or hazed beer?" text) (not= nil answer)]
  =>
  (.setColor ?q (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "ale-low-full-taste-color"))

(defrule ale-low-full-taste-color-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Ale" typeBs) (= "Low" strength) (not= nil color) (= "With full taste" taste)]
  =>
  (.setOrigin ?q (.getAnswer ?q))
  (.setPrice ?q true)
  ;;(.setEnd ?q true)
  (println "ale-low-full-taste-color-end"))

(defrule ale-strong-sweet-bitter-end
  [?q <- Question (= "You most like your beer?" text) (not= "With full taste" answer) (= "Ale" typeBs) (= "Strong" strength)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setOrigin ?q "Imported")
  (.setPrice ?q true)
  ;;(.setEnd ?q true)
  (println "ale-strong-sweet-bitter-end"))

(defrule ale-strong-full-taste
  [?q <- Question (= "You most like your beer?" text) (= "With full taste" answer) (= "Ale" typeBs) (= "Strong" strength)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setPrice ?q true)
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "ale-strong-full-taste"))

(defrule ale-strong-full-taste-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer) (= "Ale" typeBs) (= "Strong" strength) (= "With full taste" taste)]
  =>
  (.setOrigin ?q (.getAnswer ?q))
  ;;(.setEnd ?q true)
  (println "ale-strong-full-taste-end"))

(defrule domestic-imported
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (not= nil answer)]
  =>
  (.setOrigin ?q (.getAnswer ?q))
  (.setText ?q "Would you spend more money on beer?")
  (.setSuggestedAnswers ?q ["Yes" "No"])
  (println "domestic-imported"))

(defrule price-cheap
  [?q <- Question (= "Would you spend more money on beer?" text) (= "No" answer)]
  =>
  (.setPrice ?q false)
  ;;(.setEnd ?q true)
  (println "price-cheap"))

(defrule price-expensive
  [?q <- Question (= "Would you spend more money on beer?" text) (= "Yes" answer)]
  =>
  (.setPrice ?q true)
  ;;(.setEnd ?q true)
  (println "price-expensive"))

; -------------------------------------suggesting-beer-------------------------------------

(defrule ale-low-sweet
  [?q <- Question (= "Ale" typeBs) (= "Low" strength) (= "Sweet" taste)]
  =>
  (.setNameBs ?q "Belgian Pale Ale")
  (println "ale-low-sweet"))

(defrule ale-low-bitter
  [?q <- Question (= "Ale" typeBs) (= "Low" strength) (= "Bitter" taste)]
  =>
  (.setNameBs ?q "Indian Pale Ale")
  (println "ale-low-bitter"))

(defrule ale-low-drinkable
  [?q <- Question (= "Ale" typeBs) (= "Low" strength) (= "Drinkable" taste)]
  =>
  (.setNameBs ?q "Witbier")
  (println "ale-low-drinkable"))

(defrule ale-low-full-taste-light
  [?q <- Question (= "Ale" typeBs) (= "Low" strength) (= "With full taste" taste) (= "Light" color)]
  =>
  (.setNameBs ?q "Kristalweizen")
  (println "ale-low-full-taste-light"))

(defrule ale-low-full-taste-hazed
  [?q <- Question (= "Ale" typeBs) (= "Low" strength) (= "With full taste" taste) (= "Hazed" color)]
  =>
  (.setNameBs ?q "Heffeweizen")
  (println "ale-low-full-taste-hazed"))

(defrule ale-low-full-taste-dark
  [?q <- Question (= "Ale" typeBs) (= "Low" strength) (= "With full taste" taste) (= "Dark" color)]
  =>
  (.setNameBs ?q "Dunkelweizen")
  (println "ale-low-full-taste-dark"))

(defrule ale-strong-sweet
  [?q <- Question (= "Ale" typeBs) (= "Strong" strength) (= "Sweet" taste)]
  =>
  (.setNameBs ?q "Belgian Strong Ale")
  (println "ale-strong-sweet"))

(defrule ale-strong-full-taste
  [?q <- Question (= "Ale" typeBs) (= "Strong" strength) (= "With full taste" taste)]
  =>
  (.setNameBs ?q "Stout/Porter")
  (println "ale-strong-full-taste"))

(defrule ale-strong-bitter
  [?q <- Question (= "Ale" typeBs) (= "Strong" strength) (= "Bitter" taste)]
  =>
  (.setNameBs ?q "Scocth Ale")
  (println "ale-strong-bitter"))

(defrule lager-light-bitter
  [?q <- Question (= "Lager" typeBs) (= "Light" color) (= "Bitter" taste)]
  =>
  (.setNameBs ?q "Pilsner")
  (println "lager-light-bitter"))

(defrule lager-light-sweet
  [?q <- Question (= "Lager" typeBs) (= "Light" color) (= "Sweet" taste)]
  =>
  (.setNameBs ?q "Kellerbier")
  (println "lager-light-sweet"))

(defrule lager-light-drinkable
  [?q <- Question (= "Lager" typeBs) (= "Light" color) (= "Drinkable" taste)]
  =>
  (.setNameBs ?q "Pale Lager")
  (println "lager-light-drinkable"))

(defrule lager-dark-low
  [?q <- Question (= "Lager" typeBs) (= "Dark" color) (= "Low" strength)]
  =>
  (.setNameBs ?q "Dark Lager")
  (println "lager-dark-low"))

(defrule lager-dark-strong
  [?q <- Question (= "Lager" typeBs) (= "Dark" color) (= "Strong" strength)]
  =>
  (.setNameBs ?q "Bock")
  (println "lager-dark-strong"))

(defn ask-question [q]
  (-> (mk-session 'beer.models.rule)
    (insert
      q)
    (fire-rules)))

