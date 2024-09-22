package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.Buy;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.Sell;
import pl.kathelan.cryptosageapp.zonda.services.OrderBookService;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletInitializationServiceUnitTest {

    @Mock
    private WalletAmountService walletAmountService;

    @InjectMocks
    private WalletInitializationService walletInitializationService;


    @Test
    void initializeWallets_initializesUninitializedCryptoPairs() {
        Set<CryptoPair> alreadyInitializedPairs = EnumSet.of(CryptoPair.BTC_PLN, CryptoPair.ETH_PLN, CryptoPair.SOL_PLN);
        when(walletAmountService.getInitializedCryptoPairs()).thenReturn(alreadyInitializedPairs);

        Map<CryptoPair, BigDecimal> holdings = new HashMap<>();

        walletInitializationService.initializeWallets(holdings);

        for (CryptoPair cryptoPair : CryptoPair.values()) {
            if (!alreadyInitializedPairs.contains(cryptoPair)) {
                assertEquals(BigDecimal.ZERO, holdings.get(cryptoPair), "Holdings should be initialized to zero for uninitiated crypto pairs.");
                verify(walletAmountService).initWalletAmount(cryptoPair);
            }
        }

        for (CryptoPair cryptoPair : alreadyInitializedPairs) {
            assertNull(holdings.get(cryptoPair), "Holdings for already initialized pairs should not be modified.");
        }
    }

}