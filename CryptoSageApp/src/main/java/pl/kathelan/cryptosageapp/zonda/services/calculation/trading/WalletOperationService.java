package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletOperationService {

    private final WalletInitializationService initializeWalletsService;
    private final TradingService tradingService;


    public void performOperations(Map<CryptoPair, Signal> signalMap) {
        initializeWalletsService.initializeWallets();
        signalMap.forEach(this::processSignal);
    }

    private void processSignal(CryptoPair cryptoPair, Signal signal) {
        try {
            switch (signal) {
                case BUY -> tradingService.buyCrypto(cryptoPair);
                case SELL -> tradingService.sellCrypto(cryptoPair);
                case HOLD -> log.info("Holding position for {}", cryptoPair);
                default -> log.warn("Unknown signal for {}: {}", cryptoPair, signal);
            }
        } catch (Exception e) {
            log.error("Error performing operation for {}: {}", cryptoPair, e.getMessage(), e);
        }
    }
}
