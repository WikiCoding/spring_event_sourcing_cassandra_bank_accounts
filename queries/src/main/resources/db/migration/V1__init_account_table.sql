CREATE TABLE IF NOT EXISTS account (
                         account_number VARCHAR(255) PRIMARY KEY,
                         account_name   VARCHAR(255) NOT NULL,
                         balance        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                         created_at     BIGINT NOT NULL,
                         version        INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_created_at ON account(created_at);