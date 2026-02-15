package com.wikicoding.bank_account_projections_and_recommendations.repository;

import com.wikicoding.bank_account_projections_and_recommendations.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Account, String> {
}
