-- name: count-cards
-- Counts the number of records existent in the cards table
SELECT COUNT(*) FROM cards;

-- name: select-latest-cards
-- Selects latest cards
SELECT * FROM cards WHERE id IN (SELECT DISTINCT ON (ancestor_id) id FROM cards ORDER BY ancestor_id DESC, id DESC LIMIT :limit) ORDER BY id DESC;


-- name: find-card-by-id
-- Find a card by id
SELECT * FROM cards WHERE id = :id LIMIT 1;

-- name: insert-card<!
-- Inserts a new record into the cards table
INSERT INTO cards (title, contents) VALUES (:title, :contents);

-- name: insert-card-with-ancestor<!
-- Inserts a new record with an ancestor_id into the cards table
INSERT INTO cards (title, contents, ancestor_id) VALUES (:title, :contents, :ancestor_id);

-- name: delete-card-by-id!
-- Deletes the card that has the given id
DELETE FROM cards WHERE id = :id;
