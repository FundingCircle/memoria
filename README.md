# memoria

![and i swear that i...](http://i.imgur.com/yUUFcNq.jpg)

## Synopsis

Memoria is a web application which provides a central location to store, search and share knowledge cards.

### Functionalities

* Supports markdown
* Search with PostgreSQL
* Google Authentication

### Roadmap

* Browse edit history
* Email domain authorization

## Dependencies

- PostgreSQL >= 9.4.x
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

## User Authentication

Memoria provides user authentication through the Google authentication API. There is not much control about who can access the cards, as long as they have a valid Google account. This provides us enough information to track who created or edited a card, but may be a problem if you need more strict security. If that's the case, consider running the application in a different network, thus limiting access.

For the user authentication to work properly, the following environment variables are required:

| Name                          | Description                       | Default value |
|-------------------------------+-----------------------------------+---------------|
| MEMORIA_GOOGLE_AUTH_CLIENT_ID | Client ID for the Google Auth API |               |
| MEMORIA_GOOGLE_AUTH_API_KEY   | API Key for the Google Auth API   |               |

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

Distributed under the BSD 3-Clause License.
