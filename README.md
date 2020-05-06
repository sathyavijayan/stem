# stem
'stem' is a simple templating library for clojure. stem was created to
offer a simpler, light weight alternative to template rendering.

## Notice of relocation
'stem' is moving to a new organisation and moving to a 'sane'
versioning system. The library now uses
[sci](https://github.com/borkdude/sci) to render expressions.

``` clojure
[sats/stem "0.1.0-alpha1"]
```

### Backward compatibility
The previous version of the API is available in the
`stem.deprecated.core` namespace. Just require this namespace instead
of `stem.core`. This namespace will be removed in future releases, and
the new API has powerful features.

## Usage
```clojure
[satssats/stem "0.1.0-alpha1"]

;; In your namespace:
(ns my.ns
    (:require [sats.stem.core :refer :all]))
```

### Rendering a stem template
stem template supports `variables` and `expressions`.

#### Variables
Variables in the template must be wrapped within `{{ }}`. For eg:
`{{ name }}`. stem substitues variables with values supplied in the
data map.

```clojure
(render-string "Hello {{ name }}" {:name "sathya"})
; => "Hello sathya"
```

#### Expressions
Expressions must be wrapped within `{% %}`. Expressions are valid
clojure expression supported by the
[sci](https://github.com/borkdude/sci) library. Variables can be used
inside expressions.

```clojure
(render-string "Hello {% (capitalize {{ name }}) %}"
               {:name "sathya"}
               :bindings
               {'capitalize clojure.string/capitalize})
; => Hello Sathya

(render-string "{% (capitalize {{ name }}) %} lives in {% (get {{ country-names }} {{ country-code }}) %}."
               {:user-name :sathya, :country-code :uk :country-names {:uk "United Kingdom"}}
               :bindings
               {'capitalize clojure.string/capitalize})
; => Sathya lives in United Kingdom
```

### Roadmap
* Useful functions to make template authoring easier.
* Render files.
* Caching and speed improvements.


## License

Copyright © 2015-2017 Sathyavijayan Vittal

Distributed under the Apache License v 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
