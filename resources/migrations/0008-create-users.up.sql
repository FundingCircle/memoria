CREATE TABLE users (
    id serial primary key,
    google_id varchar(100),
    display_name varchar(120),
    email varchar(120),
    photo_url varchar(255),
    created_at timestamp
);

CREATE UNIQUE INDEX users_google_id_idx ON users (google_id);
