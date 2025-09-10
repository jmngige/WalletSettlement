package com.presta.walletsettlement.wallet.repo;

import com.presta.walletsettlement.wallet.domain.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    @Transactional
    @Rollback
    void testFindByWalletIdWithPessimisticLock() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("WLT-001");
        wallet.setBalance(new BigDecimal("500.00"));
        wallet.setCreatedAt(LocalDate.now());
        walletRepository.save(wallet);
        Optional<Wallet> result = walletRepository.findByWalletId("WLT-001");

        assertThat(result).isPresent();
        assertThat(result.get().getWalletId()).isEqualTo("WLT-001");
        assertThat(result.get().getBalance()).isEqualByComparingTo("500.00");
    }

    @Test
    @Transactional
    @Rollback
    void testFindByWalletIdNotFound() {
        Optional<Wallet> result = walletRepository.findByWalletId("WLT-002");
        assertThat(result).isNotPresent();
    }
}