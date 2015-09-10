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

This will compile all your cljs files (implementation and tests). When the browser is opened, it will connect to the figwheel REPL, load your files and run your tests.

Files and tests will be reloaded every time a change is saved.

CSS files will also be automatically reloaded.

## Testing

In REPL (example)

```clojure
(use 'memoria.entities.cards-test :reload)
(run-tests 'memoria.entities.cards-test)
```

To run ClojureScript tests, there are two different approaches:

1. Run `lein trampoline cljsbuild test`. This is slow but is useful to run the tests in a CI server.
2. Run lein-fighweel and watch the test results in the browser console. Tests will be automatically ran every time cljs code changes.

## If you need to change/update SemanticUI

Follow the instructions in the [SemanticUI documentation](http://semantic-ui.com/introduction/getting-started.html)

## License

Copyright © 2015 Funding Circle Ltd.
