ALTER TABLE cards DROP COLUMN tsv;
DROP TRIGGER cards_tsv_update ON cards;
DROP FUNCTION cards_search_trigger();
