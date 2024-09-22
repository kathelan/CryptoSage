package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.services.HoldingService;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingServiceUnitTest {
    @Mock
    private WalletAmountService walletAmountService;
    @Mock
    private PriceService priceService;

    @Mock
    private HoldingService holdingService;

    @InjectMocks
    private TradingService tradingService;

    @Test
    void buyCrypto_whenPriceIsValid_performsTransaction() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        WalletAmount walletAmount = new WalletAmount(BigDecimal.valueOf(1000));
        Holding holding = new Holding(BigDecimal.ZERO);
        when(walletAmountService.getWalletAmountByCryptoPair(cryptoPair)).thenReturn(walletAmount);
        when(holdingService.getHoldingByWalletAmount(walletAmount)).thenReturn(holding);
        BigDecimal price = BigDecimal.valueOf(10);
        when(priceService.getPrice(cryptoPair)).thenReturn(price);

        BigDecimal amountToBuy = walletAmount.getAmount().divide(price, 8, RoundingMode.HALF_UP);
        BigDecimal amountToUpdate = walletAmount.getAmount().subtract(price.multiply(amountToBuy)).setScale(8, RoundingMode.HALF_UP);
        WalletAmount walletAmountUpdated = new WalletAmount(amountToUpdate);
        when(walletAmountService.updateWalletAmount(amountToUpdate, walletAmount.getId())).thenReturn(walletAmountUpdated);

        tradingService.buyCrypto(cryptoPair);

        verify(holdingService).updateHolding(eq(amountToBuy), eq(holding.getId()));
        verify(walletAmountService).updateWalletAmount(eq(amountToUpdate), eq(walletAmount.getId()));
    }


    @Test
    void sellCrypto_whenPriceIsValidAndHoldingsExist_performsTransaction() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        BigDecimal initialAmount = BigDecimal.valueOf(1000);
        BigDecimal amountToSell = BigDecimal.valueOf(10);
        BigDecimal price = BigDecimal.valueOf(50);
        BigDecimal expectedNewAmount = initialAmount.subtract(price.multiply(amountToSell)).setScale(8, RoundingMode.HALF_UP);

        WalletAmount walletAmountDb = new WalletAmount(initialAmount);
        Holding holdings = new Holding(amountToSell);
        when(walletAmountService.getWalletAmountByCryptoPair(cryptoPair)).thenReturn(walletAmountDb);
        when(holdingService.getHoldingByWalletAmount(walletAmountDb)).thenReturn(holdings);
        when(priceService.getPrice(cryptoPair)).thenReturn(price);

        WalletAmount updatedWalletAmount = new WalletAmount(expectedNewAmount);
        when(walletAmountService.updateWalletAmount(expectedNewAmount, walletAmountDb.getId())).thenReturn(updatedWalletAmount);

        tradingService.sellCrypto(cryptoPair);

        verify(holdingService).updateHolding(eq(BigDecimal.ZERO), eq(holdings.getId()));
        verify(walletAmountService).updateWalletAmount(expectedNewAmount, walletAmountDb.getId());
        assertEquals(expectedNewAmount, updatedWalletAmount.getAmount(), "Wallet amount should be correctly updated.");
    }

    @Test
    void sellCrypto_whenPriceIsNull_doesNotPerformTransaction() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        BigDecimal walletAmountValue = BigDecimal.valueOf(1000);
        WalletAmount walletAmount = new WalletAmount(walletAmountValue);
        Holding holding = new Holding(BigDecimal.valueOf(10));

        when(walletAmountService.getWalletAmountByCryptoPair(cryptoPair)).thenReturn(walletAmount);
        when(holdingService.getHoldingByWalletAmount(walletAmount)).thenReturn(holding);
        when(priceService.getPrice(cryptoPair)).thenReturn(null);

        tradingService.sellCrypto(cryptoPair);

        verify(walletAmountService, never()).updateWalletAmount(any(), any());
        verify(holdingService, never()).updateHolding(any(), anyLong());
    }

    @Test
    void buyCrypto_whenPriceIsNull_doesNotPerformTransaction() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        WalletAmount walletAmount = new WalletAmount(BigDecimal.valueOf(1000));
        Holding holding = new Holding(BigDecimal.ZERO);

        when(walletAmountService.getWalletAmountByCryptoPair(cryptoPair)).thenReturn(walletAmount);
        when(holdingService.getHoldingByWalletAmount(walletAmount)).thenReturn(holding);
        when(priceService.getPrice(cryptoPair)).thenReturn(null);

        tradingService.buyCrypto(cryptoPair);

        verify(walletAmountService, never()).updateWalletAmount(any(), any());
        verify(holdingService, never()).updateHolding(any(), anyLong());
    }

    @Test
    void buyCrypto_whenPriceIsZero_doesNotPerformTransaction() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        WalletAmount walletAmount = new WalletAmount(BigDecimal.valueOf(1000));
        Holding holding = new Holding(BigDecimal.ZERO);

        when(walletAmountService.getWalletAmountByCryptoPair(cryptoPair)).thenReturn(walletAmount);
        when(holdingService.getHoldingByWalletAmount(walletAmount)).thenReturn(holding);
        when(priceService.getPrice(cryptoPair)).thenReturn(BigDecimal.ZERO);

        tradingService.buyCrypto(cryptoPair);

        verify(walletAmountService, never()).updateWalletAmount(any(), any());
        verify(holdingService, never()).updateHolding(any(), anyLong());
    }
}
