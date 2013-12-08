(ns so.modernized.colder-wars.util)


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
  {:added "1.0"}
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
                   "with-open only allows Symbols in bindings"))))
