(ns sats.stem.core
  (:require [clojure.string :as str]
            [sci.core :as sci]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                         ----==| U T I L S |==----                          ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- deep-merge
  "Like merge, but merges maps recursively."
  [& maps]
  (let [maps (filter (comp not nil?) maps)]
    (if (every? map? maps)
      (apply merge-with deep-merge maps)
      (last maps))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                 ----==| P R E P R O C E S S I N G |==----                  ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def ^:private var-pattern
  #"(?<!\\)(?:\{\{)\s(.*?)\s(?:\}\})")


(def ^:private expr-pattern
  #"(?<!\\)(?:\{%)\s((?s).*?)\s(?:%\})")


(def ^:private default-options
  {:bindings {'join-with clojure.string/join}})


(def ^:private data-key '___data___)


(defn- tokenise-exprs
  [s]
  (re-seq expr-pattern s))


(defn- preprocess-vars
  "Replaces vars which look like {{ var }} to {% (:var
  <replacement_symbol>) %}"
  [s]
  (str/replace s var-pattern
               (format "{%% (:$1 %s) %%}" data-key)))


(defn- preprocess-expr
  [s]
  (str/replace s var-pattern
               (format "(:$1 %s)" data-key)))


(defn- preprocess-exprs
  [s]
  (let [tokens (re-seq expr-pattern s)]
    (reduce
     (fn [s [match expr]]
       (->> expr
            preprocess-expr
            (format "{%% %s %%}")
            (str/replace s match)))
     s
     tokens)))



(def ^:private preprocess
  (comp preprocess-vars
     preprocess-exprs))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                   ----==| E V A L / R E N D E R |==----                    ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- evaluate-expressions
  [{:keys [bindings ignore-missing-data?] :as opts} s data]
  (let [bindings' (assoc bindings data-key data)
        tokens    (tokenise-exprs s)]
    (reduce
      (fn [s [match expr]]
        (if-let [replacement (sci/eval-string expr {:bindings bindings'})]
          (str/replace s match replacement)
          (if ignore-missing-data?
            (str/replace s match "")
            (throw (ex-info "token missing" {:error :token-missing})))))
      s
      tokens)))


(defn render-string
  [s data & opts]
  (let [opts (->> opts
                  (apply hash-map)
                  (deep-merge default-options))
        preprocessed (preprocess s)]
    (evaluate-expressions opts preprocessed data)))


(comment

  (render-string
    "Hello {{ name }}. Please select a product.\n{% (join-with \"\n\" (for [p {{ products }}] (name (:name p)))) %}"
    {:name "John"
     :products
     [{:name :ipad}
      {:name :phone}
      {:name :oculus}]}
    :bindings {})

  (render-string
    "Hello {% (get {:male \"Mr\" :other \"Mx\"} {{ gender }}) %}.{% (capitalize {{ name }}) %}"
    {:name "sathya" :gender :male}
    :bindings
    {'capitalize clojure.string/capitalize})

  ;;

  )
