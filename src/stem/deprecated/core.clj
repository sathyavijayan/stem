(ns stem.deprecated.core
  (:require [clojure.edn :as edn]
            [clojure.string :as str]))

;; STEM - simple templating engine.
;;
;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                      ---==| V A R I A B L E S |==----                      ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn tokenise-vars
  [s]
  (re-seq #"\$\{([^}]+)\}" s))



(defn replace-var
  [bindings s [match replacement-key :as token]]
  (if-let [replacement-value (get bindings (keyword replacement-key))]
    (str/replace s match replacement-value)
    (throw (ex-info "No binding for token" {:token match}))))



(defn render-vars
  [bindings s]
  (let [tokens (tokenise-vars s)]
    (reduce (partial replace-var bindings) s tokens)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                    ---==| E X P R E S S I O N S |==----                    ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn tokenise-exprs
  [s]
  (re-seq #"\%\{([^}]+)\}" s))



(defn eval-expr
  [bindings expr-str]
  (let [expr  (edn/read-string expr-str)
        fk    (first expr)
        f     (and fk (get bindings fk nil))
        args  (seq (rest expr))]

    (when-not f
      (throw (ex-info "No binding for token" {:token fk})))

    (if args
      (apply f args)
      (f))))



(defn replace-expr
  [bindings s [match expr-str :as token]]
  (if-let [replacement-value (str (eval-expr bindings expr-str))]
    (str/replace s match replacement-value)
    (throw (ex-info "Expression evaluated to nil" {:token match}))))



(defn render-exprs
  [bindings s]
  (let [tokens (tokenise-exprs s)]
    (reduce (partial replace-expr bindings) s tokens)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                     ---==| P U B L I C   A P I |==----                     ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn render-string
  [bindings s]
  (when s
    (->> (render-vars bindings s)
         (render-exprs bindings))))
