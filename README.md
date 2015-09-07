# memoria

![and i swear that i...](http://i.imgur.com/yUUFcNq.jpg)

## Dependencies

- PostgreSQL
- JDK 1.8

## Installation

- Clone the project.
- Create the databases:

```
createdb memoria_dev
createdb memoria_test
```

- Migrate the database

## Running the Ring server

```
lein trampoline ring server
```

```
brew install leiningen
createdb memoria_test
createdb memoria_dev
lein deps
```

Run migrations, in REPL:

```
(require '[memoria.db :as db])
(db/migrate-db)
(db/migrate-db :test)
```

Run development server

```
lein trampoline ring server
```

## Testing

In REPL (example)

```
(use 'memoria.entities.cards-test :reload)
(run-tests 'memoria.entities.cards-test)
```

## Usage

FIXME: explanation

    $ java -jar memory_lane-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2015 Funding Circle Ltd.

Distributed under the BSD 3-Clause License.
