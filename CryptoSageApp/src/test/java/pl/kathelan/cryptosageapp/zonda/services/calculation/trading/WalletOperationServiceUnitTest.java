package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletOperationServiceUnitTest {

    @Mock
    private WalletInitializationService walletInitializationService;

    @Mock
    private TradingService tradingService;

    @InjectMocks
    private WalletOperationService walletOperationService;


    @Test
    void performOperations_initializesAndProcessesSignals() {
        Map<CryptoPair, Signal> signalMap = Map.of(
                CryptoPair.BTC_PLN, Signal.BUY,
                CryptoPair.ETH_PLN, Signal.SELL,
                CryptoPair.SOL_PLN, Signal.HOLD
        );

        walletOperationService.performOperations(signalMap);

        verify(walletInitializationService).initializeWallets();
        verify(tradingService).buyCrypto(eq(CryptoPair.BTC_PLN));
        verify(tradingService).sellCrypto(eq(CryptoPair.ETH_PLN));
    }

    @Test
    void processSignal_handlesUnknownSignal() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        Signal unknownSignal = Signal.UNKNOWN;

        walletOperationService.performOperations(Map.of(cryptoPair, unknownSignal));

        verifyNoMoreInteractions(tradingService);
    }

    @Test
    void processSignal_doesNotChangeStateOnError() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        Signal buySignal = Signal.BUY;
        Map<CryptoPair, BigDecimal> originalHoldings = new HashMap<>(Map.of(cryptoPair, BigDecimal.valueOf(1000)));

        doThrow(new RuntimeException("Failure during trading")).when(tradingService).buyCrypto(eq(cryptoPair));

        try {
            walletOperationService.performOperations(Map.of(cryptoPair, buySignal));
        } catch (Exception ignored) {
        }

        assertEquals(BigDecimal.valueOf(1000), originalHoldings.get(cryptoPair), "Holdings should remain unchanged after an error.");
    }
}