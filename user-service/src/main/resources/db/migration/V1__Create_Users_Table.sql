-- V1__Create_Users_Table.sql
-- Create users table for User Service

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create users table
CREATE TABLE users (
                       id varchar(36) NOT NULL,
                       auth_server_user_id varchar(36) NOT NULL UNIQUE,
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

-- Create user_roles table for ElementCollection
CREATE TABLE user_roles (
                            user_id varchar(36) NOT NULL,
                            role_name varchar(100) NOT NULL,
                            PRIMARY KEY (user_id, role_name),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_auth_server_user_id ON users(auth_server_user_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_name ON user_roles(role_name);

-- Add comments for documentation
COMMENT ON TABLE users IS 'Main users table containing user profile information';
COMMENT ON COLUMN users.auth_server_user_id IS 'Reference to user ID from Auth Server';
COMMENT ON COLUMN users.email IS 'User email address - must be unique';
COMMENT ON COLUMN users.version IS 'Optimistic locking version field';

COMMENT ON TABLE user_roles IS 'User roles mapping table (ElementCollection)';
COMMENT ON COLUMN user_roles.role_name IS 'Role name assigned to user';