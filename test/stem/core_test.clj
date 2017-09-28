(ns stem.core-test
  (:require [midje.sweet :refer :all]
            [stem.core :refer :all]
            [clojure.string :as str])
  (:import [java.time LocalDate]))



(defn today
  []
  (LocalDate/now))



(defn quotation
  []
  (constantly "அறம் செய்ய விரும்பு"))


(def DEFAULT-BINDINGS
  {:name "sathya" :hello "hello"})



(tabular
 (fact "render works properly"
       (render-string (merge DEFAULT-BINDINGS ?addl-bindings) ?template) => ?outcome)
 ?template                           ?addl-bindings                                       ?outcome
 "template with no vars or exprs"    nil                                                  "template with no vars or exprs"
 "${hello}"                          nil                                                  "hello"
 "${hello} ${name}!"                 nil                                                  "hello sathya!"
 "${hello} world ${name}!"           nil                                                  "hello world sathya!"
 "%{(quotation)}"                    nil                                                  (throws Exception)
 "%{(quotation)}"                   {:quotation "stem.core-test/quotation"}               (quotation))
