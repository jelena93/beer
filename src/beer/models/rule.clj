(ns beer.models.rule
  (:require [compojure.core :refer :all]
            [beer.models.question :refer :all]
            [clara.rules :refer :all])
  (:import [beer.models.question Question]))

(defrule location
  [?q <- Question (= nil text) (= nil answer)]
  =>
  (.setText ?q "Where would you rather drink beer?")
  (.setOrigin ?q 0)
  (.setPrice ?q 0)
  (.setSuggestedAnswers ?q ["At home" "In front of a building" "In a pub" "In a club"])
  (println "location"))

(defrule location-building
  [?q <- Question (= "Where would you rather drink beer?" text) (= "In front of a building" answer)]
  =>
  (.setStyleType ?q "Lager")
  (.setPrice ?q 1)
  (.setOrigin ?q 1)
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
  (.setStyleType ?q "Lager")
  (.setText ?q "Would you rather drink light or dark beer?")
  (.setSuggestedAnswers ?q ["Light" "Dark"])
  (println "type-lager"))

(defrule type-ale
  [?q <- Question (= "Do you prefer more classic or specific tastes?" text) (= "Specific" answer)]
  =>
  (.setStyleType ?q "Ale")
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
  (println "lager-dark-location-building"))

(defrule lager-light-taste
  [?q <- Question (= "You most like your beer?" text) (not= nil answer) (= "Lager" styleType) (not= "In front of a building" location)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "lager-light-taste"))

(defrule lager-dark-strength
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" styleType) (not= "In front of a building" location)]
  =>
  (.setStrength ?q (.getAnswer ?q))
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "lager-dark-strength"))

(defrule lager-light-location-building-end
  [?q <- Question (= "You most like your beer?" text) (not= nil answer) (= "Lager" styleType) (= "In front of a building" location)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (println "lager-light-location-building-end"))

(defrule lager-dark-strength-location-building-end
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (not= nil answer) (= "Lager" styleType) (= "In front of a building" location)]
  =>
  (.setStrength ?q (.getAnswer ?q))
  (println "lager-dark-strength-location-building-end"))

(defrule ale-strong
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (= "Strong" answer) (= "Ale" styleType)]
  =>
  (.setStrength ?q (.getAnswer ?q))
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "With full taste"])
  (println "ale-strong"))

(defrule ale-low
  [?q <- Question (= "Do you prefer low or strong alcohol drinks?" text) (= "Low" answer) (= "Ale" styleType)]
  =>
  (.setStrength ?q (.getAnswer ?q))
  (.setText ?q "You most like your beer?")
  (.setSuggestedAnswers ?q ["Bitter" "Sweet" "Drinkable" "With full taste"])
  (println "ale-low"))

(defrule domestic-Bock-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Domestic" answer) (= "Lager" styleType) (= "Strong" strength)]
  =>
  (.setOrigin ?q 1)
  (.setPrice ?q 0)
  (println "domestic-Bock-end"))

(defrule imported-Bock-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Imported" answer) (= "Lager" styleType) (= "Strong" strength)]
  =>
  (.setOrigin ?q 0)
  (.setPrice ?q 0)
  (println "imported-Bock-end"))

(defrule domestic-Kellerbier-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Domestic" answer) (= "Lager" styleType) (= "Sweet" taste)]
  =>
  (.setOrigin ?q 1)
  (.setPrice 0)
  (println "domestic-Kellerbier-end"))

(defrule imported-Kellerbier-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Imported" answer) (= "Lager" styleType) (= "Sweet" taste)]
  =>
  (.setOrigin ?q 0)
  (.setPrice 0)
  (println "imported-Kellerbier-end"))

(defrule ale-low-full-taste
  [?q <- Question (= "You most like your beer?" text) (= "With full taste" answer) (= "Ale" styleType) (= "Low" strength)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setText ?q "Would you rather drink light, dark or hazed beer?")
  (.setSuggestedAnswers ?q ["Light" "Dark" "Hazed"])
  (println "ale-low-full-taste"))

(defrule ale-low-not-full-taste
  [?q <- Question (= "You most like your beer?" text) (not= "With full taste" answer) (= "Ale" styleType) (= "Low" strength)]
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

(defrule ale-domestic-low-full-taste-color-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Domestic" answer) (= "Ale" styleType) (= "Low" strength) (not= nil color) (= "With full taste" taste)]
  =>
  (.setOrigin ?q 1)
  (.setPrice ?q 0)
  (println "ale-domestic-low-full-taste-color-end"))

(defrule ale-imported-low-full-taste-color-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Imported" answer) (= "Ale" styleType) (= "Low" strength) (not= nil color) (= "With full taste" taste)]
  =>
  (.setOrigin ?q 0)
  (.setPrice ?q 0)
  (println "ale-imported-low-full-taste-color-end"))

(defrule ale-strong-sweet-bitter-end
  [?q <- Question (= "You most like your beer?" text) (not= "With full taste" answer) (= "Ale" styleType) (= "Strong" strength)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setOrigin ?q 0)
  (.setPrice ?q 0)
  (println "ale-strong-sweet-bitter-end"))

(defrule ale-strong-full-taste
  [?q <- Question (= "You most like your beer?" text) (= "With full taste" answer) (= "Ale" styleType) (= "Strong" strength)]
  =>
  (.setTaste ?q (.getAnswer ?q))
  (.setPrice ?q 0)
  (.setText ?q "Would you rather buy a domestic or imported product?")
  (.setSuggestedAnswers ?q ["Domestic" "Imported"])
  (println "ale-strong-full-taste"))

(defrule ale-domestic-strong-full-taste-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Domestic" answer) (= "Ale" styleType) (= "Strong" strength) (= "With full taste" taste)]
  =>
  (.setOrigin ?q 1)
  (println "ale-domestic-strong-full-taste-end"))

(defrule ale-imported-strong-full-taste-end
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Imported" answer) (= "Ale" styleType) (= "Strong" strength) (= "With full taste" taste)]
  =>
  (.setOrigin ?q 0)
  (println "ale-imported-strong-full-taste-end"))

(defrule domestic
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Domestic" answer)]
  =>
  (.setOrigin ?q 1)
  (.setText ?q "Would you spend more money on beer?")
  (.setSuggestedAnswers ?q ["Yes" "No"])
  (println "domestic"))

(defrule imported
  [?q <- Question (= "Would you rather buy a domestic or imported product?" text) (= "Imported" answer)]
  =>
  (.setOrigin ?q 0)
  (.setText ?q "Would you spend more money on beer?")
  (.setSuggestedAnswers ?q ["Yes" "No"])
  (println "imported"))

(defrule price-cheap
  [?q <- Question (= "Would you spend more money on beer?" text) (= "No" answer)]
  =>
  (.setPrice ?q 1)
  (println "price-cheap"))

(defrule price-expensive
  [?q <- Question (= "Would you spend more money on beer?" text) (= "Yes" answer)]
  =>
  (.setPrice ?q 0)
  (println "price-expensive"))

; -------------------------------------suggesting-beer-------------------------------------

(defrule ale-low-sweet
  [?q <- Question (= "Ale" styleType) (= "Low" strength) (= "Sweet" taste)]
  =>
  (.setStyleName ?q "Belgian Pale Ale")
  (println "ale-low-sweet"))

(defrule ale-low-bitter
  [?q <- Question (= "Ale" styleType) (= "Low" strength) (= "Bitter" taste)]
  =>
  (.setStyleName ?q "Indian Pale Ale")
  (println "ale-low-bitter"))

(defrule ale-low-drinkable
  [?q <- Question (= "Ale" styleType) (= "Low" strength) (= "Drinkable" taste)]
  =>
  (.setStyleName ?q "Witbier")
  (println "ale-low-drinkable"))

(defrule ale-low-full-taste-light
  [?q <- Question (= "Ale" styleType) (= "Low" strength) (= "With full taste" taste) (= "Light" color)]
  =>
  (.setStyleName ?q "Kristalweizen")
  (println "ale-low-full-taste-light"))

(defrule ale-low-full-taste-hazed
  [?q <- Question (= "Ale" styleType) (= "Low" strength) (= "With full taste" taste) (= "Hazed" color)]
  =>
  (.setStyleName ?q "Heffeweizen")
  (println "ale-low-full-taste-hazed"))

(defrule ale-low-full-taste-dark
  [?q <- Question (= "Ale" styleType) (= "Low" strength) (= "With full taste" taste) (= "Dark" color)]
  =>
  (.setStyleName ?q "Dunkelweizen")
  (println "ale-low-full-taste-dark"))

(defrule ale-strong-sweet
  [?q <- Question (= "Ale" styleType) (= "Strong" strength) (= "Sweet" taste)]
  =>
  (.setStyleName ?q "Belgian Strong Ale")
  (println "ale-strong-sweet"))

(defrule ale-strong-full-taste
  [?q <- Question (= "Ale" styleType) (= "Strong" strength) (= "With full taste" taste)]
  =>
  (.setStyleName ?q "Stout/Porter")
  (println "ale-strong-full-taste"))

(defrule ale-strong-bitter
  [?q <- Question (= "Ale" styleType) (= "Strong" strength) (= "Bitter" taste)]
  =>
  (.setStyleName ?q "Scocth Ale")
  (println "ale-strong-bitter"))

(defrule lager-light-bitter
  [?q <- Question (= "Lager" styleType) (= "Light" color) (= "Bitter" taste)]
  =>
  (.setStyleName ?q "Pilsner")
  (println "lager-light-bitter"))

(defrule lager-light-sweet
  [?q <- Question (= "Lager" styleType) (= "Light" color) (= "Sweet" taste)]
  =>
  (.setStyleName ?q "Kellerbier")
  (println "lager-light-sweet"))

(defrule lager-light-drinkable
  [?q <- Question (= "Lager" styleType) (= "Light" color) (= "Drinkable" taste)]
  =>
  (.setStyleName ?q "Pale Lager")
  (println "lager-light-drinkable"))

(defrule lager-dark-low
  [?q <- Question (= "Lager" styleType) (= "Dark" color) (= "Low" strength)]
  =>
  (.setStyleName ?q "Dark Lager")
  (println "lager-dark-low"))

(defrule lager-dark-strong
  [?q <- Question (= "Lager" styleType) (= "Dark" color) (= "Strong" strength)]
  =>
  (.setStyleName ?q "Bock")
  (println "lager-dark-strong"))

(defquery get-question []
  [?q <- Question])

(defn show-question [session]
  (query session get-question))

(defn ask-question [q]
  (println "pre:" q)
  (-> (mk-session 'beer.models.rule)
      (insert q)
      (fire-rules)
      (show-question)))
