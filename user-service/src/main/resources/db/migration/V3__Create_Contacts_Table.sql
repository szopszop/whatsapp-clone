CREATE TABLE contacts (
    -- BaseEntity
                          id uuid PRIMARY KEY,
                          created_at timestamptz,
                          created_by VARCHAR(255),
                          updated_at timestamptz,
                          updated_by VARCHAR(255),
                          version INTEGER NOT NULL DEFAULT 0,

    -- Contact Entity
                          user_id UUID NOT NULL,
                          contact_id UUID NOT NULL,
                          conversation_id UUID,

    -- Foreign keys
                          CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          CONSTRAINT fk_contact FOREIGN KEY (contact_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Unique constraint
                          CONSTRAINT unique_user_contact UNIQUE (user_id, contact_id)
);

-- Indexes
CREATE INDEX idx_contacts_user_id ON contacts(user_id);
CREATE INDEX idx_contacts_contact_id ON contacts(contact_id);