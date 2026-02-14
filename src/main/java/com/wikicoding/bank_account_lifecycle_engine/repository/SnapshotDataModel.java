package com.wikicoding.bank_account_lifecycle_engine.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "snapshot", keyspace = "snapshots_keyspace")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SnapshotDataModel {
    @PrimaryKey @Column("account_number")
    private String accountNumber;
    @Column("account_name")
    private String accountName;
    @Column("balance")
    private double balance;
    @Column("created_at")
    private long createdAt;
    @Column("version")
    private int version;
}
