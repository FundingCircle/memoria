-- call nextval to allow currval to be called further below, ID sequence remains unchanged
DO $$
BEGIN
  PERFORM setval('cards_id_seq', nextval('cards_id_seq'), false);
END$$;

ALTER TABLE cards ADD COLUMN ancestor_id integer DEFAULT currval('cards_id_seq') NOT NULL;
UPDATE cards SET ancestor_id = id;
