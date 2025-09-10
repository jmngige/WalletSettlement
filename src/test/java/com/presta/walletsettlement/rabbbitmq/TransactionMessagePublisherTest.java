package com.presta.walletsettlement.rabbbitmq;

import com.presta.walletsettlement.wallet.domain.dto.LedgerTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransactionMessagePublisherTest {

    private RabbitTemplate rabbitTemplate;
    private TransactionMessagePublisher publisher;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        publisher = new TransactionMessagePublisher(rabbitTemplate);
        publisher.exchangeName = "test-exchange";
        publisher.routingKey = "test-routing";
    }

    @Test
    void testPublishTransactionMessage() {
        LedgerTransaction transaction = LedgerTransaction.builder()
                .walletId("WLT-001")
                .amount(new BigDecimal("250.00"))
                .transactionRequestId("req-CS-001")
                .transactionId("ref-CS-001")
                .description("Top-up")
                .transactionDate(LocalDate.of(2025, 9, 10))
                .build();

        publisher.publishTransactionMessage(transaction);

        ArgumentCaptor<LedgerTransaction> captor = ArgumentCaptor.forClass(LedgerTransaction.class);
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("test-exchange"), eq("test-routing"), captor.capture());

        LedgerTransaction sent = captor.getValue();
        assertThat(sent.getWalletId()).isEqualTo("WLT-001");
        assertThat(sent.getAmount()).isEqualByComparingTo("250.00");
        assertThat(sent.getTransactionId()).isEqualTo("ref-CS-001");
    }
}
