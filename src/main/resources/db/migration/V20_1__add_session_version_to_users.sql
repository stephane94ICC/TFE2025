ALTER TABLE users
    ADD COLUMN session_version INT NOT NULL DEFAULT 0,
    ADD CONSTRAINT chk_users_session_version CHECK (session_version >= 0);