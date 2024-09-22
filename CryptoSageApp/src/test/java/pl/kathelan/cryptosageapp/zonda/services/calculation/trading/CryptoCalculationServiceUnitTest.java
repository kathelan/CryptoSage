package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CryptoCalculationServiceUnitTest {

    @InjectMocks
    private CryptoCalculationService calculationService;


    @Test
    void calculateAmountToBuy_returnsCorrectValue() {
        BigDecimal walletAmount = new BigDecimal("1000");
        BigDecimal price = new BigDecimal("10");
        BigDecimal expectedAmountToBuy = new BigDecimal("100.00000000");

        BigDecimal result = calculationService.calculateAmountToBuy(walletAmount, price);
        assertEquals(expectedAmountToBuy, result, "The calculated amount to buy should be correct.");
    }

    @Test
    void calculateNewWalletAmount_returnsCorrectValue() {
        BigDecimal walletAmount = new BigDecimal("1000");
        BigDecimal amountToBuy = new BigDecimal("50");
        BigDecimal price = new BigDecimal("20");
        BigDecimal expectedNewWalletAmount = new BigDecimal("0.00000000");

        BigDecimal result = calculationService.calculateNewWalletAmount(walletAmount, amountToBuy, price);
        assertEquals(expectedNewWalletAmount, result, "The calculated new wallet amount should be correct.");
    }

    @Test
    void calculateAmountToSell_returnsCorrectValue() {
        BigDecimal walletAmount = new BigDecimal("1000");
        BigDecimal price = new BigDecimal("10");
        BigDecimal amountToSell = new BigDecimal("100");
        BigDecimal expectedAmountAfterSale = new BigDecimal("2000.00000000");

        BigDecimal result = calculationService.calculateAmountToSell(walletAmount, price, amountToSell);
        assertEquals(expectedAmountAfterSale, result, "The calculated amount after sale should be correct.");
    }
}
