CREATE KEYSPACE IF NOT EXISTS event_store
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1}
     AND durable_writes = true;

CREATE TABLE IF NOT EXISTS event_store.events
(
    account_number text,
    event_type     text,
    version        int,
    event_json     text,
    PRIMARY KEY (account_number, version)
);

CREATE KEYSPACE IF NOT EXISTS snapshots_keyspace
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1}
     AND durable_writes = true;

CREATE TABLE IF NOT EXISTS snapshots_keyspace.snapshot
(
    account_number text,
    account_name   text,
    balance        double,
    created_at     bigint,
    version        int,
    PRIMARY KEY (account_number)
);

CREATE KEYSPACE IF NOT EXISTS outbox_keyspace
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1}
     AND durable_writes = true;

CREATE TABLE IF NOT EXISTS outbox_keyspace.outbox
(
    account_number text,
    account_name   text,
    balance        double,
    created_at     bigint,
    version        int,
    PRIMARY KEY (account_number)
);