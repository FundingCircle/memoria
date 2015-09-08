ALTER TABLE cards ADD COLUMN tsv tsvector;
CREATE INDEX tsv_idx ON cards USING gin(tsv);

UPDATE cards SET tsv = setweight(to_tsvector('english', coalesce(title, '')), 'A') || setweight(to_tsvector('english', coalesce(contents, '')), 'C');

CREATE OR REPLACE FUNCTION cards_search_trigger() RETURNS trigger AS $$
begin
  new.tsv :=
    setweight(to_tsvector('english', coalesce(new.title, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(new.contents, '')), 'C');
  return new;
end
$$ LANGUAGE plpgsql;

CREATE TRIGGER cards_tsv_update BEFORE INSERT OR UPDATE ON cards FOR EACH ROW EXECUTE PROCEDURE cards_search_trigger();

