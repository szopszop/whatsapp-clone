-- V3__Insert_Initial_Roles.sql
-- Insert initial roles

INSERT INTO roles (id, name, created_at, created_by) VALUES
                                                         (gen_random_uuid(), 'ROLE_USER', CURRENT_TIMESTAMP, 'system'),
                                                         (gen_random_uuid(), 'ROLE_ADMIN', CURRENT_TIMESTAMP, 'system'),
                                                         (gen_random_uuid(), 'ROLE_MODERATOR', CURRENT_TIMESTAMP, 'system');