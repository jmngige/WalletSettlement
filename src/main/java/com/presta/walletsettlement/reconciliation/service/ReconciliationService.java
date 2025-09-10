package com.presta.walletsettlement.reconciliation.service;

import com.opencsv.CSVWriter;
import com.presta.walletsettlement.reconciliation.domain.ReconciliationResultDto;
import com.presta.walletsettlement.reconciliation.domain.ReconciliationTransaction;
import com.presta.walletsettlement.wallet.domain.model.Ledger;
import com.presta.walletsettlement.wallet.exception.ReconFIleNotFoundException;
import com.presta.walletsettlement.wallet.repo.LedgerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReconciliationService {

    private final CsvReportReader csvReportReader;
    private final LedgerRepository ledgerRepository;
    @Value("${reports.output.path}")
    private String outputDirPath;

    public ReconciliationService(CsvReportReader csvReportReader, LedgerRepository ledgerRepository) {
        this.csvReportReader = csvReportReader;
        this.ledgerRepository = ledgerRepository;
    }

    public List<ReconciliationResultDto> reconcile(String date) throws ReconFIleNotFoundException {
        List<Ledger> internalTxns = ledgerRepository.findAllByTransactionDate(date);

        // Map internal transactions to ReconciliationTransaction for matching
        List<ReconciliationTransaction> internalList = internalTxns.stream()
                .map(t -> {
                    String type = csvReportReader.resolveType(t.getTransactionId());
                    return ReconciliationTransaction.builder()
                            .transactionId(t.getTransactionId())
                            .amount(t.getAmount())
                            .type(type)
                            .build();
                })
                .toList();

        // Load external transactions report
        String fileName = "transaction_report_" + date + ".csv";
        Map<String, ReconciliationTransaction> externalTxnMap = new LinkedHashMap<>(csvReportReader.readCsvReport(fileName));

        List<ReconciliationResultDto> result = new ArrayList<>(internalList.size() + externalTxnMap.size());

        // check from internal vs external report
        for (ReconciliationTransaction internal : internalList) {
            ReconciliationTransaction external = externalTxnMap.remove(internal.getTransactionId()); // remove if found
            if (external != null) {
                if (internal.getAmount().compareTo(external.getAmount()) == 0) {
                    String status = "Match";
                    String description = "successful match";
                    result.add(toDto(internal, status, description));
                    result.add(toDto(external, status, description));
                } else {
                    String status = "Exception";
                    String description = internal.getType() + " transaction amount mismatch";
                    result.add(toDto(internal, status, description));
                    result.add(toDto(external, status, description));
                }
            } else {
                String status = "Exception";
                String description = "Missing external " + internal.getType() + " transaction";
                result.add(toDto(internal, status, description));
            }
        }

        // process any remaining external transactions missing in internal
        for (ReconciliationTransaction externalRemaining : externalTxnMap.values()) {
            String status = "Exception";
            String description = "Missing internal " + externalRemaining.getType() + " transaction";
            result.add(toDto(externalRemaining, status, description));
        }

        // Sort results by transactionId
        List<ReconciliationResultDto> sortedResult = result.stream()
                .sorted(Comparator.comparing(ReconciliationResultDto::getTransactionId))
                .collect(Collectors.toList());

        // Construct output file path
        String outputFileName = "reconciliation_" + date + ".csv";
        String outputFilePath = outputDirPath + File.separator + outputFileName;

        File outputDir = new File(outputDirPath);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            log.error("Failed to create output directory: {}", outputDirPath);
            throw new RuntimeException("Failed to create output directory: " + outputDirPath);
        }

        // Write to CSV
        writeToCsv(sortedResult, outputFilePath);

        return result;
    }

    private ReconciliationResultDto toDto(ReconciliationTransaction txn, String status, String description) {
        return ReconciliationResultDto.builder()
                .transactionId(txn.getTransactionId())
                .amount(txn.getAmount())
                .description(description)
                .reconType(txn.getType())
                .status(status)
                .build();
    }

    private void writeToCsv(List<ReconciliationResultDto> results, String outputFilePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath))) {
            // Write CSV header
            String[] header = {"transaction_id", "amount", "status", "description"};
            writer.writeNext(header);

            // Write data rows
            for (ReconciliationResultDto dto : results) {
                String[] row = {
                        dto.getTransactionId(),
                        dto.getAmount().toString(),
                        dto.getStatus(),
                        dto.getDescription()
                };
                writer.writeNext(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV file: " + outputFilePath, e);
        }
    }


}
