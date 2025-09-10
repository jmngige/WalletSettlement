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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvReportReaderTest {

    private final CsvReportReader reader = new CsvReportReader();

    @TempDir
    Path tempDir;


    @Test
    void readCsvReport_parsesFileAndResolvesTypes_fromClasspath() throws Exception {
        // create reconfiles directory on the test classpath (target/test-classes/reconfiles)
        Path reconfilesDir = Paths.get("target", "test-classes", "reconfiles");
        Files.createDirectories(reconfilesDir);

        String fileName = "transaction_report_2025-09-05.csv";
        Path file = reconfilesDir.resolve(fileName);

        String csv = "transactionId,amount\n" +
                     "ref-MP-001,100\n" +
                     "ref-CRB-001,50\n" +
                     "ref-KYC-001,75\n";


        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write(csv);
        }

        Map<String, ReconciliationTransaction> map = reader.readCsvReport(fileName);

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