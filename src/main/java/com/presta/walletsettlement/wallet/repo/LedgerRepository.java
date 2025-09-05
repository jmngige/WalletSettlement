package com.presta.walletsettlement.wallet.repo;

import com.presta.walletsettlement.wallet.domain.dto.LedgerTransaction;
import com.presta.walletsettlement.wallet.domain.model.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    boolean existsByTransactionRequestId(String transactionRequestId);

    @Query(nativeQuery = true, value = """
            select * from ledger_transactions where DATE(transaction_date) = :date
            """)
    List<Ledger> findAllByTransactionDate(String date);
}
