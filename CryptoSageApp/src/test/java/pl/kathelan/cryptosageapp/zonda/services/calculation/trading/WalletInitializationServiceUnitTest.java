package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.services.HoldingService;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletInitializationServiceUnitTest {

    @Mock
    private WalletAmountService walletAmountService;

    @Mock
    private HoldingService holdingService;

    @InjectMocks
    private WalletInitializationService walletInitializationService;


    @Test
    void initializeWallets_initializesUninitializedCryptoPairs() {
        Set<CryptoPair> alreadyInitializedPairs = EnumSet.of(CryptoPair.BTC_PLN, CryptoPair.ETH_PLN, CryptoPair.SOL_PLN);

        for (CryptoPair cryptoPair : CryptoPair.values()) {
            if (!alreadyInitializedPairs.contains(cryptoPair)) {
                WalletAmount mockWalletAmount = new WalletAmount();
                when(walletAmountService.initWalletAmount(cryptoPair)).thenReturn(mockWalletAmount);

                walletInitializationService.initializeWallets();

                verify(walletAmountService).initWalletAmount(cryptoPair);
                verify(holdingService).initHoldingsAmount(mockWalletAmount, BigDecimal.ZERO);
            }
        }
    }
}