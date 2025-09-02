package com.presta.walletsettlement.wallet.api;

import com.presta.walletsettlement.wallet.domain.dto.request.ConsumeRequest;
import com.presta.walletsettlement.wallet.domain.dto.request.TopUpRequest;
import com.presta.walletsettlement.wallet.domain.dto.response.BalanceResponse;
import com.presta.walletsettlement.wallet.domain.dto.response.ConsumeResponse;
import com.presta.walletsettlement.wallet.domain.dto.response.TopUpResponse;
import com.presta.walletsettlement.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<TopUpResponse> topUp(@PathVariable("id") Long id, @Valid @RequestBody TopUpRequest request) {
        TopUpResponse response = walletService.topUp(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/consume")
    public ResponseEntity<ConsumeResponse> consume(@PathVariable("id") Long id, @Valid @RequestBody ConsumeRequest request) {
        ConsumeResponse response = walletService.consume(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable("id") Long id) {
        BalanceResponse response = walletService.getBalance(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
