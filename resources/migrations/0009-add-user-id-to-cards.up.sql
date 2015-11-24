ALTER TABLE cards ADD COLUMN user_id integer REFERENCES users (id);
