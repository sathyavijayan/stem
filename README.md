# stem
'stem' is a simple templating library for clojure. stem was created to offer a simpler, light weight alternative to template rendering.

## Usage
```clojure
[stem "1.0.3"]

;; In your namespace:
(ns my.ns
    (:require [stem.core :refer :all]))
```

### Rendering a stem template
stem template supports `variables` and `expressions`.

#### Variables
Variables in the template must be wrapped within `${}`. For eg:`${name}`. stem substitues variables with values supplied in the bindings map.
```
;; returns "Hello, World"
;; values for variables must be supplied in the bindings map, keyed by
;; variable name (as keyword).
(let [bindings {:name "World"}]
    (render-string bindings "Hello, ${name}"))
```
#### Expressions
Expressions must be wrapped within `%{}`. Expressions are simple s-expressions and support any function that is declared in the bindings map.  For eg:
```clojure
;; returns "HELLO, WORLD"
;; functions used in expressions must also be declared in the bindings map
;; keyed by the function name/alias (as symbol).
(let [bindings {'capitalize clojure.string/capitalize}]
    (render-string bindings "Hello, %{(capitalize \"Hello, World\"}"))

;; variables can be used within expressions
;; returns "Hello, WORLD"
(let [bindings {:name       "world"
                'capitalize 'clojure.string/capitalize}]
    (render-string bindings "Hello, %{(capitalize \"${name}\"}"))
```

### Roadmap
* Utility functions to return analysis of a template.
* Support to render files.


## License

Copyright © 2015-2017 Sathyavijayan Vittal

Distributed under the Apache License v 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
