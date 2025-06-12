-- V3__Insert_Initial_Roles.sql
-- Insert initial roles

INSERT INTO roles (id, name, created_at, created_by) VALUES
                                                         (gen_random_uuid()::varchar, 'ROLE_USER', CURRENT_TIMESTAMP, 'system'),
                                                         (gen_random_uuid()::varchar, 'ROLE_ADMIN', CURRENT_TIMESTAMP, 'system'),
                                                         (gen_random_uuid()::varchar, 'ROLE_MODERATOR', CURRENT_TIMESTAMP, 'system');