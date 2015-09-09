# memoria

![and i swear that i...](http://i.imgur.com/yUUFcNq.jpg)

## Dependencies

- PostgreSQL
- JDK 1.8
- NodeJS (if you need to change/update SemanticUI)
- Gulp (if you need to change/update SemanticUI)

## Installation

- Create the databases:
```
createdb memoria_dev
createdb memoria_test
```

- Migrate the database (using a REPL session)

```clojure
(require '[memoria.db :as db])
(db/migrate-db)
(db/migrate-db :test)
```

## Running the Ring server

```
lein trampoline ring server
```

## Running lein-figwheel

```
lein figwheel dev
```

## Testing

In REPL (example)

```clojure
(use 'memoria.entities.cards-test :reload)
(run-tests 'memoria.entities.cards-test)
```

## If you need to change/update SemanticUI

Follow the instructions in the [SemanticUI documentation](http://semantic-ui.com/introduction/getting-started.html)

## License

Copyright © 2015 Funding Circle Ltd.

Distributed under the BSD 3-Clause License.
