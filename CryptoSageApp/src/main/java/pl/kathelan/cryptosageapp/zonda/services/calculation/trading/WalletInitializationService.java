package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletInitializationService {
    private final WalletAmountService walletAmountService;

    public void initializeWallets(Map<CryptoPair, BigDecimal> cryptoHoldings) {
        Set<CryptoPair> initializedPairs = walletAmountService.getInitializedCryptoPairs();
        for (CryptoPair cryptoPair : CryptoPair.values()) {
            if (!initializedPairs.contains(cryptoPair)) {
                cryptoHoldings.put(cryptoPair, BigDecimal.ZERO);
                walletAmountService.initWalletAmount(cryptoPair);
                log.info("created holdings for {} with value: {}", cryptoPair, cryptoHoldings.get(cryptoPair));
            }
        }
    }

}
