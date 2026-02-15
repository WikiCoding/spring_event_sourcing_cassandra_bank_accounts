package com.wikicoding.bank_account_lifecycle_engine.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "outbox", keyspace = "outbox_keyspace")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutboxDataModel {
    @PrimaryKeyColumn(name = "account_number", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private String accountNumber;
    @Column("account_name")
    private String accountName;
    @Column("balance")
    private double balance;
    @PrimaryKeyColumn(name = "version", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private int version;
    @PrimaryKeyColumn(name = "processed", type = PrimaryKeyType.PARTITIONED)
    private boolean processed;
}
