package com.presta.walletsettlement.wallet.repo;

import com.presta.walletsettlement.wallet.domain.model.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    boolean existsByTransactionRequestId(String transactionRequestId);
}
