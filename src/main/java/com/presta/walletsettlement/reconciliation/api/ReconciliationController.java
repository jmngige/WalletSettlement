package com.presta.walletsettlement.reconciliation.api;

import com.presta.walletsettlement.reconciliation.domain.ReconciliationResultDto;
import com.presta.walletsettlement.reconciliation.service.ReconciliationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/v1/reconciliation")
@Tag(name = "Reconciliation API", description = "API for reconciliation operations")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("report")
    public ResponseEntity<List<ReconciliationResultDto>> report( @Parameter(description = "Date in YYYY-MM-DD format") @RequestParam String date) {
        return ResponseEntity.ok(reconciliationService.reconcile(date));
    }

}
