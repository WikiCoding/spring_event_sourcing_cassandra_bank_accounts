package com.wikicoding.bank_account_lifecycle_engine.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "outbox", keyspace = "outbox_keyspace")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutboxDataModel {
    /**
     * I'll just allow any new events coming in to upsert the latest outbox entry.
     * Then just sends the latest version once for each accountNumber and then clears the database entries
     * **/
    @PrimaryKey @Column("account_number")
    private String accountNumber;
    @Column("account_name")
    private String accountName;
    @Column("balance")
    private double balance;
    @Column("createdAt")
    private long createdAt;
    @Column("version")
    private int version;
}
