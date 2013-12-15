(ns so.modernized.colder-wars.util
  (:require [clojure.string :as str]))

; A generic protocol for objects that need to have their memory allocation
; managed manually.
(defprotocol Disposable
  (dispose [this]))


; Shamelessly yoinked from clojure/core.clj
(defmacro ^{:private true} assert-args
  [& pairs]
  `(do (when-not ~(first pairs)
         (throw (IllegalArgumentException.
                  (str (first ~'&form) " requires " ~(second pairs) " in " ~'*ns* ":" (:line (meta ~'&form))))))
     ~(let [more (nnext pairs)]
        (when more
          (list* `assert-args more)))))

; Shamelessly yoinked from with-open in clojure/core.clj
(defmacro with-disposal
  "bindings => [name init ...]

  Evaluates body in a try expression with names bound to the values
  of the inits, and a finally clause that calls (.dispose name) on each
  name in reverse order."
  [bindings & body]
  (assert-args
     (vector? bindings) "a vector for its binding"
     (even? (count bindings)) "an even number of forms in binding vector")
  (cond
    (= (count bindings) 0) `(do ~@body)
    (symbol? (bindings 0)) `(let ~(subvec bindings 0 2)
                              (try
                                (with-disposal ~(subvec bindings 2) ~@body)
                                (finally
                                  (.dispose ~(bindings 0)))))
    :else (throw (IllegalArgumentException.
                   "with-disposal only allows Symbols in bindings"))))

(defn- camelize
  [sym]
  (-> sym (str/replace #"-(\w)" (comp str/upper-case second))))

(map (comp (partial list '.) symbol (partial str "get") camelize str/capitalize) ['motion-state 'world-transform])

(defmacro safely-get-in
  [set-into m fields]
  (let [getters (map (comp (partial list '.) symbol (partial str "get") camelize str/capitalize) fields)]
  `(.set ~set-into (-> ~m
                     ~@getters))))
