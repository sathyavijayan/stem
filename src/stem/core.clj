(ns stem.core
  (:require [clojure.string :as str]))

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



(defn replace-expr
  [bindings s [match expr-str :as token]]
  (if-let [replacement-value (eval (read-string expr-str))]
    (str/replace s match replacement-value)
    (throw (ex-info "Expression evaluated to nil" {:token match}))))



(defn render-exprs
  [bindings s]
  (let [tokens (tokenise-exprs s)]
    (reduce (partial replace-expr bindings) s tokens)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                      ---==| F U N C T I O N S |==----                      ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- tokenise-fns
  [s]
  (re-seq #"\(([^(\p{Blank}|\))]+)" s))



(defn- replace-fn
  [bindings s [match replacement-key :as token]]
  (if-let [replacement-value (get bindings (keyword replacement-key))]
    (do
      (when-not (fn? (eval (read-string replacement-value)))
        (throw (ex-info "Invalid binding for token - must be a fn"
                        {:token replacement-key})))

      (str/replace s match (str "(" replacement-value " ")))
    (throw (ex-info "No binding for token" {:token match}))))



(defn- render-fns
  [bindings s]
  (let [tokens (tokenise-fns s)]
    (reduce (partial replace-fn bindings) s tokens)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                     ---==| P U B L I C   A P I |==----                     ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn render-string
  [bindings s]
  (->> (render-vars bindings s)
       (render-fns  bindings)
       (render-exprs bindings)))
