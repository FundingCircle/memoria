ALTER TABLE cards ADD COLUMN deleted boolean default false;
UPDATE cards SET deleted = 'f';
