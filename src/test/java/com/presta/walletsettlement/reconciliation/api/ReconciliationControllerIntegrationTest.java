package com.presta.walletsettlement.reconciliation.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.presta.walletsettlement.reconciliation.domain.ReconciliationResultDto;
import com.presta.walletsettlement.reconciliation.service.ReconciliationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ReconciliationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReconciliationService reconciliationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void reportEndpoint_returnsOkAndJson() throws Exception {
        String date = "2025-09-10";
        ReconciliationResultDto dto = ReconciliationResultDto.builder()
                .transactionId("ref-MP-001")
                .amount(new BigDecimal("100.00"))
                .reconType("MPESA_TOPUP")
                .status("Match")
                .description("successful match")
                .build();

        when(reconciliationService.reconcile(date)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/reconciliation/report")
                        .param("date", date)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].transactionId").value("ref-MP-001"))
                .andExpect(jsonPath("$[0].status").value("Match"));
    }

}