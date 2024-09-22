package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.services.HoldingService;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;

import java.math.BigDecimal;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletInitializationService {
    private final WalletAmountService walletAmountService;
    private final HoldingService holdingService;

    @Transactional
    public void initializeWallets() {
        Set<CryptoPair> initializedPairs = walletAmountService.getInitializedCryptoPairs();
        for (CryptoPair cryptoPair : CryptoPair.values()) {
            if (!initializedPairs.contains(cryptoPair)) {
                WalletAmount walletAmount = walletAmountService.initWalletAmount(cryptoPair);
                holdingService.initHoldingsAmount(walletAmount ,BigDecimal.ZERO);
            }
        }
    }

}
