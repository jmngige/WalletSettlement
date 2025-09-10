package com.presta.walletsettlement.reconciliation.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.presta.walletsettlement.reconciliation.domain.ReconciliationTransaction;
import com.presta.walletsettlement.wallet.exception.ReconFIleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CsvReportReader {

    @Value("${reports.input.path}")
    private String filePath;

    public Map<String, ReconciliationTransaction> readCsvReport(String fileName) {
        Map<String, ReconciliationTransaction> reportData = new HashMap<>();

        Resource resource = new ClassPathResource("reconfiles/" + fileName);
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            // Skip header
            csvReader.skip(1);

            String[] parts;
            while ((parts = csvReader.readNext()) != null) {
                // Validate row length (expect at least transactionId and amount)
                if (parts.length < 2) {
                    System.err.println("Skipping invalid row: " + String.join(",", parts));
                    continue;
                }

                String transactionId = parts[0].trim();
                if (transactionId.isEmpty()) {
                    System.err.println("Skipping row with empty transactionId");
                    continue;
                }

                BigDecimal amount;
                try {
                    amount = new BigDecimal(parts[1].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Skipping row with invalid amount: " + parts[1]);
                    continue;
                }

                // get type from transactionId
                String type = resolveType(transactionId);

                reportData.put(transactionId, ReconciliationTransaction.builder()
                        .transactionId(transactionId)
                        .amount(amount)
                        .type(type)
                        .build());
            }
        } catch (FileNotFoundException e) {
            throw new ReconFIleNotFoundException("Reconciliation file " + fileName + " for selected not found for processing.");
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Error reading reconciliation CSV file: " + filePath, e);
        }
        return reportData;
    }

    public String resolveType(String transactionId) {
        if (transactionId.contains("-MP-")) {
            return "MPESA_TOPUP";
        } else if (transactionId.contains("-CRB-")) {
            return "CRB_CHECK";
        } else if (transactionId.contains("-KYC-")) {
            return "KYC_CHECK";
        } else if (transactionId.contains("-CS-")) {
            return "CREDIT_SCORE";
        }
        return "UNKNOWN";
    }

}
