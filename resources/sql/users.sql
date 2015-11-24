-- name: insert-user<!
-- Inserts a new user into the users table
INSERT INTO users (google_id, display_name, email, photo_url, created_at) VALUES (:google_id, :display_name, :email, :photo_url, :created_at)

-- name: find-user-by-google-id
-- Searches a user with the given google_id
SELECT * FROM users WHERE google_id = :google_id LIMIT 1;

-- name: count-users
-- Returns the total number of users in the database
SELECT COUNT(1) FROM users;
