-- V2__Add_User_Tables.sql
-- User management tables

CREATE TABLE roles (
                       id varchar(36) NOT NULL,
                       name varchar(255) NOT NULL UNIQUE,
                       created_at TIMESTAMP,
                       created_by varchar(255),
                       updated_at TIMESTAMP,
                       updated_by varchar(255),
                       PRIMARY KEY (id)
);

CREATE TABLE auth_users (
                            id varchar(36) NOT NULL,
                            email varchar(255) NOT NULL UNIQUE,
                            password varchar(255) NOT NULL,
                            created_at TIMESTAMP,
                            created_by varchar(255),
                            updated_at TIMESTAMP,
                            updated_by varchar(255),
                            PRIMARY KEY (id)
);

-- Junction table for user-role relationship
CREATE TABLE auth_user_roles (
                                 user_id varchar(36) NOT NULL,
                                 role_id varchar(36) NOT NULL,
                                 PRIMARY KEY (user_id, role_id),
                                 FOREIGN KEY (user_id) REFERENCES auth_users(id) ON DELETE CASCADE,
                                 FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_users_email ON auth_users(email);
CREATE INDEX idx_roles_name ON roles(name);
CREATE INDEX idx_auth_user_roles_user_id ON auth_user_roles(user_id);
CREATE INDEX idx_auth_user_roles_role_id ON auth_user_roles(role_id);