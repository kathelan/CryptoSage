package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.*;
import pl.kathelan.cryptosageapp.zonda.repositories.CryptoCurrencyPairRepository;
import pl.kathelan.cryptosageapp.zonda.repositories.HoldingRepository;
import pl.kathelan.cryptosageapp.zonda.repositories.WalletAmountRepository;
import pl.kathelan.cryptosageapp.zonda.services.HoldingService;
import pl.kathelan.cryptosageapp.zonda.services.TransactionHistoryService;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;
import pl.kathelan.notificationmodule.EmailNotificationChannel;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Import(TestPriceServiceConfiguration.class)
@Transactional
class TradingServiceIntegrationTest {

    @Autowired
    private TradingService tradingService;

    @Autowired
    private WalletAmountService walletAmountService;

    @Autowired
    private HoldingService holdingService;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private CryptoCalculationService calculationService;

    @Autowired
    private PriceService priceService;
    @Autowired
    private WalletAmountRepository walletAmountRepository;
    @Autowired
    private CryptoCurrencyPairRepository cryptoCurrencyPairRepository;
    @Autowired
    private HoldingRepository holdingRepository;

    private CryptoPair cryptoPair;
    private BigDecimal initialWalletAmount;
    private BigDecimal initialHoldingAmount;
    private BigDecimal testPrice;

    @BeforeEach
    public void setUp() {
        // Inicjalizacja danych testowych
        cryptoPair = CryptoPair.BTC_PLN;
        initialWalletAmount = new BigDecimal("10000.00");
        initialHoldingAmount = BigDecimal.ZERO;
        //Tworzenie cryptoCurrencyPair
        CryptoCurrencyPair cryptoCurrencyPair = new CryptoCurrencyPair();
        cryptoCurrencyPair.setCryptoPair(cryptoPair);
        cryptoCurrencyPair.setPriceRecords(Set.of(new PriceRecord(5.0, cryptoCurrencyPair)));
        cryptoCurrencyPairRepository.save(cryptoCurrencyPair);

        // Pobranie testowej ceny z testowego PriceService
        testPrice = priceService.getPrice(cryptoPair);

        // Tworzenie WalletAmount
        WalletAmount walletAmount = new WalletAmount();
        walletAmount.setCryptoCurrencyPair(cryptoCurrencyPair);
        walletAmount.setAmount(initialWalletAmount);
        walletAmountRepository.save(walletAmount);

        // Tworzenie Holding
        Holding holding = new Holding();
        holding.setWalletAmount(walletAmount);
        holding.setChangeAmount(initialHoldingAmount);
        holdingRepository.save(holding);
    }

    @Test
    void testBuyCrypto() {
        // Wykonanie operacji zakupu
        tradingService.buyCrypto(cryptoPair);

        // Pobranie zaktualizowanych danych
        WalletAmount updatedWalletAmount = walletAmountService.getWalletAmountByCryptoPair(cryptoPair);
        Holding updatedHolding = holdingService.getHoldingByWalletAmount(updatedWalletAmount);

        // Oczekiwane obliczenia
        BigDecimal amountToBuy = calculationService.calculateAmountToBuy(initialWalletAmount, testPrice);
        BigDecimal expectedWalletAmount = calculationService.calculateNewWalletAmount(
                initialWalletAmount, amountToBuy, testPrice);

        // Weryfikacja kwoty w portfelu
        assertEquals(0, expectedWalletAmount.compareTo(updatedWalletAmount.getAmount()),
                "Kwota w portfelu powinna być zaktualizowana poprawnie po zakupie kryptowaluty.");

        // Weryfikacja kwoty w holding
        assertEquals(0, amountToBuy.compareTo(updatedHolding.getChangeAmount()),
                "Kwota w holding powinna odzwierciedlać zakupioną ilość.");

        // Weryfikacja historii transakcji
        Optional<TransactionHistory> transactionHistory = transactionHistoryService
                .getLatestTransactionByHoldingId(updatedHolding.getId());
        assertEquals(amountToBuy, transactionHistory.get().getTotalValue(),
                "Historia transakcji powinna zawierać poprawną ilość zakupioną.");
        assertEquals(testPrice, transactionHistory.get().getPricePerUnit(),
                "Historia transakcji powinna zawierać poprawną cenę.");
    }

    @Test
    void testSellCrypto() {
        // Przygotowanie - użytkownik posiada kryptowalutę
        WalletAmount walletAmount = walletAmountService.getWalletAmountByCryptoPair(cryptoPair);
        walletAmount.setAmount(BigDecimal.ZERO);
        walletAmount = walletAmountRepository.save(walletAmount);
        Holding holding = holdingService.getHoldingByWalletAmount(walletAmount);
        BigDecimal initialCryptoAmount = new BigDecimal("0.2"); // Użytkownik posiada 0.2 BTC
        holdingService.updateHolding(initialCryptoAmount, holding.getId(), null);

        // Wykonanie operacji sprzedaży
        tradingService.sellCrypto(cryptoPair);

        // Pobranie zaktualizowanych danych
        WalletAmount updatedWalletAmount = walletAmountService.getWalletAmountByCryptoPair(cryptoPair);
        Holding updatedHolding = holdingService.getHoldingByWalletAmount(updatedWalletAmount);

        // Oczekiwane obliczenia
        BigDecimal expectedWalletAmount = calculationService.calculateAmountToSell(
                BigDecimal.ZERO, testPrice, initialCryptoAmount);

        // Weryfikacja kwoty w portfelu
        assertEquals(0, expectedWalletAmount.compareTo(walletAmount.getAmount()),
                "Kwota w portfelu powinna być zaktualizowana poprawnie po sprzedaży kryptowaluty.");

        // Weryfikacja kwoty w holding (powinna być zero po sprzedaży)
        assertEquals(0, BigDecimal.ZERO.compareTo(updatedHolding.getChangeAmount()),
                "Kwota w holding powinna wynosić zero po sprzedaży całej kryptowaluty.");

        // Weryfikacja historii transakcji
        Optional<TransactionHistory> transactionHistory = transactionHistoryService
                .getLatestTransactionByHoldingId(updatedHolding.getId());
        assertEquals(initialCryptoAmount, transactionHistory.get().getTotalValue(),
                "Historia transakcji powinna zawierać poprawną ilość sprzedaną.");
        assertEquals(testPrice, transactionHistory.get().getPricePerUnit(),
                "Historia transakcji powinna zawierać poprawną cenę.");
    }
}
