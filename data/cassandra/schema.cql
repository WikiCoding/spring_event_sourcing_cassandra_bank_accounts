CREATE KEYSPACE if not exists event_store
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1}
        AND durable_writes = true;

CREATE TABLE IF NOT EXISTS event_store.events (
                        event_id text,
                        account_number text,
                        event_type text,
                        version int,
                        event_json text,
                        PRIMARY KEY (account_number, event_id)
);