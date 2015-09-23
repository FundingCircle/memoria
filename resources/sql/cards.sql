-- name: count-cards
-- Counts the number of records existent in the cards table
SELECT COUNT(*) FROM cards WHERE deleted = 'f';

-- name: select-latest-cards
-- Selects latest cards
SELECT id, title, tags, contents, created_at FROM cards WHERE id IN (SELECT DISTINCT ON (ancestor_id) id FROM cards ORDER BY ancestor_id DESC, id DESC LIMIT :limit OFFSET :offset) AND deleted = 'f' ORDER BY id DESC;

-- name: search-cards
-- Searches card using PostgreSQL full text search. Read http://shisaa.jp/postset/postgresql-full-text-search-part-3.html to
-- better understand what it does.
SELECT id, title, tags, contents, created_at, ts_rank(tsv, tsquery, 1), created_at as rank FROM cards, to_tsquery(:query) tsquery WHERE tsquery @@ tsv AND deleted = 'f' GROUP BY tsquery, id HAVING id IN (SELECT DISTINCT ON (ancestor_id) id FROM cards ORDER BY ancestor_id DESC, id DESC) ORDER BY rank DESC, id DESC LIMIT :limit OFFSET :offset;

-- name: find-card-by-id
-- Find a card by id
SELECT id, title, tags, contents, ancestor_id, created_at FROM cards WHERE id = :id AND deleted = :deleted LIMIT 1;

-- name: insert-card<!
-- Inserts a new record into the cards table
INSERT INTO cards (title, tags, contents, created_at) VALUES (:title, :tags, :contents, :created_at);

-- name: insert-card-with-ancestor<!
-- Inserts a new record with an ancestor_id into the cards table
INSERT INTO cards (title, tags, contents, ancestor_id, created_at) VALUES (:title, :tags, :contents, :ancestor_id, :created_at);

-- name: soft-delete-card-by-id!
-- Marks a card as soft deleted
UPDATE cards SET deleted = 't' WHERE id = :id;

