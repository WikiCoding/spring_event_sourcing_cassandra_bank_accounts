package com.wikicoding.bank_account_lifecycle_engine.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "events", keyspace = "event_store")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDataModel {
    @PrimaryKeyColumn(name = "account_number", type = PrimaryKeyType.PARTITIONED)
    private String accountNumber;
    @PrimaryKeyColumn(name = "version", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    @Column("version")
    private int version;
    @Column("event_type")
    private String eventType;
    @Column("event_json")
    private String eventJson;
}
