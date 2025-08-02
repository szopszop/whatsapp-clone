-- V3__Create_User_Fcm_Tokens_Table.sql
-- Create table for storing user's FCM device tokens

CREATE TABLE user_fcm_tokens (
                                 user_id VARCHAR(36) NOT NULL,
                                 fcm_token VARCHAR(255) NOT NULL,
                                 PRIMARY KEY (user_id, fcm_token),
                                 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_fcm_tokens_user_id ON user_fcm_tokens(user_id);