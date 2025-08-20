CREATE TABLE user_fcm_tokens (
                                 user_id UUID NOT NULL,
                                 fcm_token VARCHAR(255) NOT NULL,
                                 PRIMARY KEY (user_id, fcm_token),
                                 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_fcm_tokens_user_id ON user_fcm_tokens(user_id);