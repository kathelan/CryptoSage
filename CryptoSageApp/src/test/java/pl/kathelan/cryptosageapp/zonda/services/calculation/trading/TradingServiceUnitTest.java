package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.TransactionHistory;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.services.HoldingService;
import pl.kathelan.cryptosageapp.zonda.services.TransactionHistoryService;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static pl.kathelan.cryptosageapp.zonda.model.TransactionType.BUY;
import static pl.kathelan.cryptosageapp.zonda.model.TransactionType.SELL;

@ExtendWith(MockitoExtension.class)
class TradingServiceUnitTest {
    @Mock
    private WalletAmountService walletAmountService;
    @Mock
    private PriceService priceService;
    @Mock
    private CryptoCalculationService cryptoCalculationService;
    @Mock
    private HoldingService holdingService;
    @Mock
    private TransactionHistoryService transactionHistoryService;

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
        when(cryptoCalculationService.calculateAmountToBuy(any(), any())).thenReturn(amountToBuy);
        when(cryptoCalculationService.calculateNewWalletAmount(any(), any(), any())).thenReturn(amountToUpdate);

        TransactionHistory transactionHistory = new TransactionHistory();
        when(transactionHistoryService.createHistoryTransaction(any(), any(), any(), any())).thenReturn(transactionHistory);

        tradingService.buyCrypto(cryptoPair);

        verify(holdingService).updateHolding(eq(amountToBuy), eq(holding.getId()), eq(transactionHistory));
        verify(walletAmountService).updateWalletAmount(eq(amountToUpdate), eq(walletAmount.getId()));
        verify(transactionHistoryService).createHistoryTransaction(eq(BUY), eq(holding), eq(price), eq(amountToBuy));
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
        when(cryptoCalculationService.calculateAmountToSell(any(), any(), any())).thenReturn(expectedNewAmount);

        WalletAmount updatedWalletAmount = new WalletAmount(expectedNewAmount);
        when(walletAmountService.updateWalletAmount(expectedNewAmount, walletAmountDb.getId())).thenReturn(updatedWalletAmount);

        TransactionHistory transactionHistory = new TransactionHistory();
        when(transactionHistoryService.createHistoryTransaction(any(), any(), any(), any())).thenReturn(transactionHistory);

        tradingService.sellCrypto(cryptoPair);

        verify(holdingService).updateHolding(eq(BigDecimal.ZERO), eq(holdings.getId()), eq(transactionHistory));
        verify(walletAmountService).updateWalletAmount(eq(expectedNewAmount), eq(walletAmountDb.getId()));
        verify(transactionHistoryService).createHistoryTransaction(eq(SELL), eq(holdings), eq(price), eq(holdings.getChangeAmount()));
        assertEquals(expectedNewAmount, updatedWalletAmount.getAmount(), "Wallet amount should be correctly updated.");
    }
}
