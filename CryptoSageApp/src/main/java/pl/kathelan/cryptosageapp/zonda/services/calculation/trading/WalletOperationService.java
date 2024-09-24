package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;
import pl.kathelan.cryptosageapp.zonda.services.calculation.SignalListener;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletOperationService implements SignalListener {

    private final WalletInitializationService initializeWalletsService;
    private final TradingService tradingService;

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

    @Override
    public void onSignalGenerated(CryptoPair cryptoPair, Signal signal) {
        initializeWalletsService.initializeWallets();
        processSignal(cryptoPair, signal);
    }
}
