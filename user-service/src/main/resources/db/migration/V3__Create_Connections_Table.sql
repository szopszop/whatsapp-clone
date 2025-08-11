CREATE TABLE connections (
                             id UUID PRIMARY KEY,
                             requester_id UUID NOT NULL,
                             target_id UUID NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users(id),
                             CONSTRAINT fk_target FOREIGN KEY (target_id) REFERENCES users(id),
                             CONSTRAINT unique_connection UNIQUE (requester_id, target_id)
);

-- Create index for faster lookups
CREATE INDEX idx_connections_requester ON connections(requester_id);
CREATE INDEX idx_connections_target ON connections(target_id);
CREATE INDEX idx_connections_status ON connections(status);