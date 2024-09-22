package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.services.HoldingService;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingService {
    private final WalletAmountService walletAmountService;
    private final HoldingService holdingService;
    private final PriceService priceService;
    private final CryptoCalculationService calculationService;

    public synchronized void buyCrypto(CryptoPair cryptoPair) {
        WalletAmount walletAmountDb = walletAmountService.getWalletAmountByCryptoPair(cryptoPair);
        Holding holdingDb = holdingService.getHoldingByWalletAmount(walletAmountDb);
        logBuyingStart(cryptoPair, walletAmountDb, holdingDb);
        BigDecimal price = validatePrice(cryptoPair, false, null);
        if (price == null) return;

        BigDecimal amountToBuy = calculationService.calculateAmountToBuy(walletAmountDb.getAmount(), price);
        BigDecimal amountToUpdate = calculationService.calculateNewWalletAmount(walletAmountDb.getAmount(), amountToBuy, price);

        WalletAmount updatedWalletAmount = walletAmountService.updateWalletAmount(amountToUpdate, walletAmountDb.getId());
        holdingService.updateHolding(amountToBuy, holdingDb.getId());
        logTransactionCompleted("Bought", cryptoPair, amountToBuy, price, updatedWalletAmount.getAmount());
        //TODO dodac historie transakcji
    }

    public synchronized void sellCrypto(CryptoPair cryptoPair) {
        WalletAmount walletAmountDb = walletAmountService.getWalletAmountByCryptoPair(cryptoPair);
        Holding holdingDb = holdingService.getHoldingByWalletAmount(walletAmountDb);
        logSellingStart(cryptoPair, walletAmountDb, holdingDb);
        BigDecimal price = validatePrice(cryptoPair, true, holdingDb.getChangeAmount());
        if (price == null) return;

        BigDecimal amountToUpdate = calculationService.calculateAmountToSell(walletAmountDb.getAmount(), price, holdingDb.getChangeAmount());
        WalletAmount updated = walletAmountService.updateWalletAmount(amountToUpdate, walletAmountDb.getId());
        holdingService.updateHolding(BigDecimal.ZERO, holdingDb.getId());
        //TODO dodac historie transakcji
        logTransactionCompleted("Sold", cryptoPair, holdingDb.getChangeAmount(), price, updated.getAmount());
    }

    private BigDecimal validatePrice(CryptoPair cryptoPair, boolean isSelling, BigDecimal holdingsAmount) {
        BigDecimal price = priceService.getPrice(cryptoPair);
        boolean priceValid = (price != null && price.compareTo(BigDecimal.ZERO) > 0);

        if (isSelling) {
            priceValid = priceValid && (holdingsAmount.compareTo(BigDecimal.ZERO) > 0);
        }

        if (!priceValid) {
            String action = isSelling ? "sell" : "buy";
            logPriceValidationFail(action, holdingsAmount, cryptoPair);
            return null;
        }
        return price;
    }

    private void logBuyingStart(CryptoPair cryptoPair, WalletAmount walletAmount, Holding holding) {
        log.info("Starting buying crypto for pair: {}, current wallet value: {} and holdings: {}", cryptoPair, walletAmount.getAmount(), holding.getChangeAmount());
    }

    private void logSellingStart(CryptoPair cryptoPair, WalletAmount walletAmount, Holding holding) {
        log.info("Starting selling crypto for pair: {}, current wallet value: {} and holdings: {}", cryptoPair, walletAmount.getAmount(), holding.getChangeAmount());
    }

    private void logTransactionCompleted(String action, CryptoPair cryptoPair, BigDecimal amount, BigDecimal price, BigDecimal remainingAmount) {
        log.info("{} {} of {} at price {}. Remaining wallet amount: {}", action, amount, cryptoPair, price, remainingAmount);
    }

    private void logPriceValidationFail(String action, BigDecimal holdingsAmount, CryptoPair cryptoPair) {
        log.warn("No {} or invalid price to {} for {}", holdingsAmount, action, cryptoPair);
    }


}
