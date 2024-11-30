package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.SignalType;

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
    void onSignalGenerated_initializesWalletsAndProcessesBuySignal() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        SignalType signalType = SignalType.BUY;

        walletOperationService.onSignalGenerated(cryptoPair, signalType);

        verify(walletInitializationService).initializeWallets();
        verify(tradingService).buyCrypto(eq(cryptoPair));
        verifyNoMoreInteractions(tradingService);
    }

    @Test
    void onSignalGenerated_initializesWalletsAndProcessesSellSignal() {
        CryptoPair cryptoPair = CryptoPair.ETH_PLN;
        SignalType signalType = SignalType.SELL;

        walletOperationService.onSignalGenerated(cryptoPair, signalType);

        verify(walletInitializationService).initializeWallets();
        verify(tradingService).sellCrypto(eq(cryptoPair));
        verifyNoMoreInteractions(tradingService);
    }

    @Test
    void onSignalGenerated_initializesWalletsAndProcessesHoldSignal() {
        CryptoPair cryptoPair = CryptoPair.SOL_PLN;
        SignalType signalType = SignalType.HOLD;

        walletOperationService.onSignalGenerated(cryptoPair, signalType);

        verify(walletInitializationService).initializeWallets();
        verifyNoInteractions(tradingService);
    }

    @Test
    void onSignalGenerated_handlesUnknownSignal() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        SignalType unknownSignalType = SignalType.UNKNOWN;

        walletOperationService.onSignalGenerated(cryptoPair, unknownSignalType);

        verify(walletInitializationService).initializeWallets();
        verifyNoInteractions(tradingService);
    }

    @Test
    void onSignalGenerated_catchesExceptionDuringTrading() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        SignalType signalType = SignalType.BUY;

        doThrow(new RuntimeException("Trading error")).when(tradingService).buyCrypto(eq(cryptoPair));

        walletOperationService.onSignalGenerated(cryptoPair, signalType);

        verify(walletInitializationService).initializeWallets();
        verify(tradingService).buyCrypto(eq(cryptoPair));
    }
}
