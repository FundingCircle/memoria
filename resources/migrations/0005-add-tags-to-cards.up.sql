ALTER TABLE cards ADD COLUMN tags VARCHAR(255);

UPDATE cards SET tsv = setweight(to_tsvector('english', coalesce(tags, '')), 'A') || setweight(to_tsvector('english', coalesce(title, '')), 'B') || setweight(to_tsvector('english', coalesce(contents, '')), 'C');

CREATE OR REPLACE FUNCTION cards_search_trigger() RETURNS trigger AS $$
begin
  new.tsv :=
    setweight(to_tsvector('english', coalesce(new.tags, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(new.title, '')), 'B') ||
    setweight(to_tsvector('english', coalesce(new.contents, '')), 'C');
  return new;
end
$$ LANGUAGE plpgsql;
