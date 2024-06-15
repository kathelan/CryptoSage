package pl.kathelan.cryptosageapp.zonda.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.services.calculation.WalletOperationService;

import java.util.Map;

@RestController
@RequestMapping("/wallets")
@AllArgsConstructor
@Slf4j
public class WalletOperationController {

    private final WalletOperationService walletOperationService;


    @GetMapping
    public ResponseEntity<Map<CryptoPair, Double>> getWalletAmounts() {
        return ResponseEntity.ok(walletOperationService.getWalletAmounts());
    }

    @GetMapping("/holdings")
    public ResponseEntity<Map<CryptoPair, Double>> getHoldings() {
        return ResponseEntity.ok(walletOperationService.getCryptoHoldings());
    }
}
