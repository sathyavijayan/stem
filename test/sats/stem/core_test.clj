(ns sats.stem.core-test
  (:require [sats.stem.core :as core]
            [midje.sweet :refer :all]
            [clojure.string :as str]))

(def fixtures
  {:multi-line-expr
   (-> "List of users"
       (str "{% (for [u users]")
       (str "\n")
       (str " (:name u)) %}"))})


(tabular
 (fact "test regex patterns"
   (re-seq (var-get ?pattern) ?input) => ?check)
 ?input                 ?pattern                     ?check
 ;; var-pattern
 "{{ var }}"            #'core/var-pattern           (contains [["{{ var }}" "var"]] )
 "hello {{ name }}"     #'core/var-pattern           (contains [["{{ name }}" "name"]] )
 "hello world"          #'core/var-pattern           nil?
 ""                     #'core/var-pattern           nil?
 "do \\{{ ignore }}"    #'core/var-pattern           nil?
 "a {{ b }} c"          #'core/var-pattern           (contains [["{{ b }}" "b"]])
 "a {{ b }}.{{ c }} d"  #'core/var-pattern           (contains [["{{ b }}" "b"] ["{{ c }}" "c"]])

 ;; expr-pattern
 "{% (inc 1) %}"        #'core/expr-pattern          (contains [["{% (inc 1) %}" "(inc 1)"]])
 "1++ = {% (inc 1) %}!" #'core/expr-pattern          (contains [["{% (inc 1) %}" "(inc 1)"]])

 ;; multi line
 (-> fixtures :multi-line-expr)    #'core/expr-pattern     (contains [["{% (for [u users]\n (:name u)) %}"
                                                                       "(for [u users]\n (:name u))"]]))

(tabular
 (fact "test preprocessing"
   (#'core/preprocess ?input) => (format ?result (var-get #'core/data-key)))
 ?input                                     ?result
 "{{ var }}"                                "{%% (:var %s) %%}"
 "{{ title }}.{{ name }}"                   "{%% (:title %s) %%}.{%% (:name ___data___) %%}"
 "{% (inc {{ val }}) %}"                    "{%% (inc (:val ___data___)) %%}"
 "{% (inc {{ a }}) %} {% (inc {{ b }}) %}"  "{%% (inc (:a ___data___)) %%} {%% (inc (:b ___data___)) %%}"
 )


(def test-bindings
  {'uppercase clojure.string/upper-case
   'capitalize clojure.string/capitalize})


(def test-data
  {:user-name "sathya"
   :country-code :uk
   :country-names
   {:uk "United Kingdom"
    :in "India"
    :sg "Singapore"}
   :programming-languages
   [{:name "Clojure"
     :creator "Rich Hickey"}
    {:name "Ruby"
     :creator "Yukihiro Matsumoto (Matz)"}
    {:name "Java"
     :creator "James Gosling"}]})


(tabular
 (fact "test render-string"
   (core/render-string ?input test-data :bindings test-bindings) => ?result)

 ?input                                                  ?result
 "Hello {{ user-name }}"                                "Hello sathya"
 "Hello {% (capitalize {{ user-name }}) %}"             "Hello Sathya"
 "Hello {% (capitalize {{ user-name }}) %}"             "Hello Sathya"

 ;;
 (-> "{% (capitalize {{ user-name }}) %} knows"
     (str " how to code using")
     (str " {% (join-with \", \"")
     (str " (for [l {{ programming-languages }}]")
     (str " (:name l))) %} etc."))
 "Sathya knows how to code using Clojure, Ruby, Java etc."

 ;;
 "{% (capitalize {{ user-name }}) %} lives in {% (get {{ country-names }} {{ country-code }}) %}."
 "Sathya lives in United Kingdom."
 ;;
 )
