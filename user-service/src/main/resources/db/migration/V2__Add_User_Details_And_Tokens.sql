ALTER TABLE users ADD COLUMN profile_image_url VARCHAR(255);
ALTER TABLE users ADD COLUMN about TEXT;
ALTER TABLE users ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'OFFLINE';

CREATE INDEX idx_users_status ON users(status);

CREATE TABLE user_fcm_tokens (
                                 user_id UUID NOT NULL,
                                 fcm_token VARCHAR(255) NOT NULL,
                                 PRIMARY KEY (user_id, fcm_token),
                                 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_fcm_tokens_user_id ON user_fcm_tokens(user_id);