package com.presta.walletsettlement.reconciliation.service;

import com.presta.walletsettlement.reconciliation.domain.ReconciliationResultDto;
import com.presta.walletsettlement.reconciliation.domain.ReconciliationTransaction;
import com.presta.walletsettlement.wallet.domain.model.Ledger;
import com.presta.walletsettlement.wallet.exception.ReconFIleNotFoundException;
import com.presta.walletsettlement.wallet.repo.LedgerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class ReconciliationServiceTest {

    @Mock
    private CsvReportReader csvReportReader;

    @Mock
    private LedgerRepository ledgerRepository;

    @InjectMocks
    private ReconciliationService reconciliationService;

    private File outputDir;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // create temp output dir and inject into service
        outputDir = Files.createTempDirectory("recon-output-").toFile();
        ReflectionTestUtils.setField(reconciliationService, "outputDirPath", outputDir.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        for (File f : Objects.requireNonNull(outputDir.listFiles())) {
            f.delete();
        }
        outputDir.delete();
    }

    @Test
    void reconcile_match_and_mismatch_and_missingExternal_producesCorrectResults_and_writesCsv() throws Exception {
        String date = "2025-09-04";

        //will used to test Match simulation
        Ledger internal1 = new Ledger();
        internal1.setTransactionId("ref-MP-001");
        internal1.setAmount(new BigDecimal("100.00"));
        internal1.setTransactionDate(LocalDate.parse(date));

        //will used to test amount mis-Match simulation
        Ledger internal2 = new Ledger();
        internal2.setTransactionId("ref-CRB-001");
        internal2.setAmount(new BigDecimal("60.00"));
        internal2.setTransactionDate(LocalDate.parse(date));

        when(ledgerRepository.findAllByTransactionDate(date)).thenReturn(List.of(internal1, internal2));

        Map<String, ReconciliationTransaction> external = new HashMap<>();
        external.put("ref-MP-001", ReconciliationTransaction.builder()
                .transactionId("ref-MP-001")
                .amount(new BigDecimal("100.00"))
                .type("MPESA_TOPUP")
                .build());
        external.put("ref-CRB-001", ReconciliationTransaction.builder()
                .transactionId("ref-CRB-001")
                .amount(new BigDecimal("50.00"))
                .type("CRB_CHECK")
                .build());

        //will used to test when one missing internally
        external.put("ref-KYC-001", ReconciliationTransaction.builder()
                .transactionId("ref-KYC-001")
                .amount(new BigDecimal("10.00"))
                .type("KYC_CHECK")
                .build());

        when(csvReportReader.readCsvReport("transaction_report_" + date + ".csv")).thenReturn(external);
        when(csvReportReader.resolveType(anyString())).thenAnswer(invocation -> {
            String tx = invocation.getArgument(0, String.class);
            if (tx.contains("-MP-")) return "MPESA_TOPUP";
            if (tx.contains("-CRB-")) return "CRB_CHECK";
            return "UNKNOWN";
        });

        List<ReconciliationResultDto> results = reconciliationService.reconcile(date);

        assertThat(results).hasSize(5);
        assertThat(results).extracting("transactionId")
                .contains("ref-MP-001", "ref-CRB-001", "ref-KYC-001");

        File outputFile = new File(outputDir, "reconciliation_" + date + ".csv");
        assertThat(outputFile).exists();

        String content = java.nio.file.Files.readString(outputFile.toPath());
        assertThat(content).contains("ref-MP-001");
        assertThat(content).contains("ref-CRB-001");
        assertThat(content).contains("ref-KYC-001");
    }

    @Test
    void reconcile_whenCsvReaderThrowsReconFileNotFound_bubblesUp() throws Exception {
        String date = "2025-09-11";
        when(ledgerRepository.findAllByTransactionDate(date)).thenReturn(Collections.emptyList());
        when(csvReportReader.readCsvReport("transaction_report_" + date + ".csv"))
                .thenThrow(new ReconFIleNotFoundException("not found"));

        assertThrows(ReconFIleNotFoundException.class, () -> reconciliationService.reconcile(date));
    }


}