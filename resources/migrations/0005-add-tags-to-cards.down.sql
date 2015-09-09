ALTER TABLE cards DROP COLUMN tags;

UPDATE cards SET tsv = setweight(to_tsvector('english', coalesce(title, '')), 'A') || setweight(to_tsvector('english', coalesce(contents, '')), 'C');

CREATE OR REPLACE FUNCTION cards_search_trigger() RETURNS trigger AS $$
begin
  new.tsv :=
    setweight(to_tsvector('english', coalesce(new.title, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(new.contents, '')), 'C');
  return new;
end
$$ LANGUAGE plpgsql;
