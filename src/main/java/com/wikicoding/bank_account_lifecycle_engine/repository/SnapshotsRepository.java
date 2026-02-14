package com.wikicoding.bank_account_lifecycle_engine.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapshotsRepository extends CassandraRepository<SnapshotDataModel, String> {
}
