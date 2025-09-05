package com.presta.walletsettlement.wallet.api;

import com.presta.walletsettlement.wallet.domain.dto.request.ConsumeRequest;
import com.presta.walletsettlement.wallet.domain.dto.request.TopUpRequest;
import com.presta.walletsettlement.wallet.domain.dto.response.BalanceResponse;
import com.presta.walletsettlement.wallet.domain.dto.response.ConsumeResponse;
import com.presta.walletsettlement.wallet.domain.dto.response.TopUpResponse;
import com.presta.walletsettlement.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("wallets")
@Tag(name = "Wallet API", description = "API for managing wallet operations")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{id}/topup")
    @Operation(summary = "Top up a wallet", description = "Add funds to a wallet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully topped up wallet"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<TopUpResponse> topUp(@PathVariable("id") String id, @Valid @RequestBody TopUpRequest request) {
        TopUpResponse response = walletService.topUp(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/consume")
    @Operation(summary = "Consume from a wallet", description = "Deduct funds from a wallet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully consumed from wallet"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient balance"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<ConsumeResponse> consume(@PathVariable("id") String id, @Valid @RequestBody ConsumeRequest request) {
        ConsumeResponse response = walletService.consume(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Get wallet balance", description = "Retrieve the balance of a wallet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved balance"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable("id") Long id) {
        BalanceResponse response = walletService.getBalance(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}