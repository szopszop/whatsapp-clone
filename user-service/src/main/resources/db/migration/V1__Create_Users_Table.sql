CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
                       id UUID NOT NULL,
                       auth_server_user_id UUID NOT NULL UNIQUE,
                       email varchar(100) NOT NULL UNIQUE,
                       first_name varchar(255),
                       last_name varchar(255),
                       created_at TIMESTAMP,
                       created_by varchar(255),
                       updated_at TIMESTAMP,
                       updated_by varchar(255),
                       version INTEGER NOT NULL DEFAULT 0,
                       PRIMARY KEY (id)
);

CREATE TABLE user_roles (
                            user_id UUID NOT NULL,
                            role_name varchar(100) NOT NULL,
                            PRIMARY KEY (user_id, role_name),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_users_auth_server_user_id ON users(auth_server_user_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_name ON user_roles(role_name);
