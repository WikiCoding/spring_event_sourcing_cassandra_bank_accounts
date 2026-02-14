package com.wikicoding.bank_account_lifecycle_engine.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends CassandraRepository<EventDataModel, String> {
    List<EventDataModel> findAllByAccountNumber(String accountNumber);
    List<EventDataModel> findAllByAccountNumberAndVersionGreaterThan(String accountNumber, int version);
}
