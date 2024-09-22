package pl.kathelan.cryptosageapp.zonda.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;
import pl.kathelan.cryptosageapp.zonda.services.calculation.trading.WalletOperationService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/wallets")
@AllArgsConstructor
@Slf4j
public class WalletOperationController {

    private final WalletOperationService walletOperationService;
    private final WalletAmountService walletAmountService;


    @GetMapping
    public ResponseEntity<Map<CryptoPair, BigDecimal>> getWalletAmounts() {
        return ResponseEntity.ok(walletAmountService.getWallets());
    }

    @GetMapping("/holdings")
    public ResponseEntity<Map<CryptoPair, BigDecimal>> getHoldings() {
        return ResponseEntity.ok(walletOperationService.getCryptoHoldings());
    }
}
