package com.presta.walletsettlement.wallet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.presta.walletsettlement.wallet.domain.dto.request.ConsumeRequest;
import com.presta.walletsettlement.wallet.domain.dto.request.TopUpRequest;
import com.presta.walletsettlement.wallet.domain.model.Wallet;
import com.presta.walletsettlement.wallet.repo.LedgerRepository;
import com.presta.walletsettlement.wallet.repo.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Wallet savedWallet;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        ledgerRepository.deleteAll();
        walletRepository.deleteAll();

        Wallet w = new Wallet();
        w.setWalletId("WLT-001");
        w.setBalance(new BigDecimal("100.00"));
        w.setCreatedAt(LocalDate.now());
        savedWallet = walletRepository.save(w);
    }


    @Test
    void topUpEndpoint_success_and_persistsLedger() throws Exception {
        TopUpRequest request = new TopUpRequest();
        request.setRequestId("req-MP-001");
        request.setTransactionId("ref-MP-001");
        request.setAmount(new BigDecimal("25.00"));

        mockMvc.perform(post("/wallets/{id}/topup", "WLT-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Wallet top-up successful"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.balance").exists());

        Wallet updated = walletRepository.findById(savedWallet.getId()).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo(new BigDecimal("125.00"));
        assertThat(ledgerRepository.findAll()).hasSize(1);
    }

    @Test
    void topUp_duplicateRequest_doesNotCreateAnotherLedger() throws Exception {
        TopUpRequest request = new TopUpRequest();
        request.setRequestId("req-MP-001");
        request.setTransactionId("dup-tx");
        request.setAmount(new BigDecimal("10.00"));

        mockMvc.perform(post("/wallets/{id}/topup", "WLT-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        long ledgerCountAfterFirst = ledgerRepository.count();

        mockMvc.perform(post("/wallets/{id}/topup", "WLT-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        // confirm idempotency check
        assertThat(ledgerRepository.count()).isEqualTo(ledgerCountAfterFirst);
    }

    @Test
    void consumeEndpoint_success_and_persistsLedger() throws Exception {
        ConsumeRequest request = new ConsumeRequest();
        request.setRequestId("req-CRB-001");
        request.setTransactionId("ref-CRB-001");
        request.setAmount(new BigDecimal("30.00"));

        mockMvc.perform(post("/wallets/{id}/consume", "WLT-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Request processed successfully"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        Wallet updated = walletRepository.findById(savedWallet.getId()).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo(new BigDecimal("70.00"));
        assertThat(ledgerRepository.findAll()).hasSize(1);
    }

    @Test
    void consume_insufficientFunds_returnsClientError_and_noLedgerCreated() throws Exception {
        ConsumeRequest request = new ConsumeRequest();
        request.setRequestId("req-CRB-001");
        request.setTransactionId("ref-CRB-001");
        request.setAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/wallets/{id}/consume", "WLT-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        assertThat(ledgerRepository.count()).isEqualTo(0);
    }

    @Test
    void getBalanceEndpoint_returnsBalance() throws Exception {
        mockMvc.perform(get("/wallets/{id}/balance", savedWallet.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(savedWallet.getId()))
                .andExpect(jsonPath("$.balance").value(100.00));
    }


}