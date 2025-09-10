package com.presta.walletsettlement.reconciliation.service;

import com.presta.walletsettlement.reconciliation.domain.ReconciliationTransaction;
import com.presta.walletsettlement.wallet.exception.ReconFIleNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvReportReaderTest {

    private final CsvReportReader reader = new CsvReportReader();

    @TempDir
    Path tempDir;


    @Test
    void readCsvReport_parsesFileAndResolvesTypes() throws Exception {
        // Arrange
        String csv = "transactionId,amount\n" +
                     "ref-MP-001,100\n" +
                     "ref-CRB-001,50\n" +
                     "ref-KYC-001,75\n";

        File file = tempDir.resolve("transaction_report_2025-09-05.csv").toFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(csv);
        }

        Field filePathField = CsvReportReader.class.getDeclaredField("filePath");
        filePathField.setAccessible(true);
        filePathField.set(reader, tempDir.toString() + File.separator);

        Map<String, ReconciliationTransaction> map = reader.readCsvReport(file.getName());
        assertThat(map).hasSize(3);
        assertThat(map).containsKeys("ref-MP-001", "ref-CRB-001","ref-KYC-001");

        assertThat(map.get("ref-MP-001").getAmount()).isEqualByComparingTo(new BigDecimal("100"));
        assertThat(map.get("ref-MP-001").getType()).isEqualTo("MPESA_TOPUP");
        assertThat(map.get("ref-CRB-001").getType()).isEqualTo("CRB_CHECK");
        assertThat(map.get("ref-KYC-001").getType()).isEqualTo("KYC_CHECK");
    }

    @Test
    void readCsvReport_missingFile_throwsReconFileNotFound() {
        String missing = "transaction_report_2025-09-06.csv";
        assertThrows(ReconFIleNotFoundException.class, () -> reader.readCsvReport(missing));
    }
}