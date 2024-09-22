package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.services.WalletAmountService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingService {
    private final WalletAmountService walletAmountService;
    private final PriceService priceService;

    public synchronized void buyCrypto(CryptoPair cryptoPair, Map<CryptoPair, BigDecimal> cryptoHoldings) {
        WalletAmount walletAmountDb = walletAmountService.getWalletAmountByCryptoPair(cryptoPair);
        log.info("Starting buying crypto for pair: {}, current wallet value: {} and holdings: {}", cryptoPair, walletAmountDb.getAmount(), cryptoHoldings.get(cryptoPair));
        BigDecimal price = priceService.getPrice(cryptoPair);
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("No funds available or invalid price to buy {}", cryptoPair);
            return;
        }

        BigDecimal amountToBuy = walletAmountDb.getAmount().divide(price, 8, RoundingMode.HALF_UP);
        log.info("walletAmount: {}, amountToBuy:{} for cryptoPair: {}", walletAmountDb, amountToBuy, cryptoPair);
        BigDecimal amountToUpdate = walletAmountDb.getAmount().subtract(price.multiply(amountToBuy)).setScale(8, RoundingMode.HALF_UP);
        WalletAmount updated = walletAmountService.updateWalletAmount(amountToUpdate, walletAmountDb.getId());
        cryptoHoldings.put(cryptoPair, cryptoHoldings.getOrDefault(cryptoPair, BigDecimal.ZERO).add(amountToBuy).setScale(8, RoundingMode.HALF_UP));
        log.info("Bought {} of {} at price {}. Remaining wallet amount: {}", amountToBuy, cryptoPair, price, updated.getAmount());
    }

    public synchronized void sellCrypto(CryptoPair cryptoPair, Map<CryptoPair, BigDecimal> cryptoHoldings) {
        WalletAmount walletAmountDb = walletAmountService.getWalletAmountByCryptoPair(cryptoPair);
        log.info("Starting selling crypto for pair: {}, current wallet value: {} and holdings: {}", cryptoPair, walletAmountDb.getAmount(), cryptoHoldings.get(cryptoPair));
        BigDecimal amountToSell = cryptoHoldings.getOrDefault(cryptoPair, BigDecimal.ZERO);
        BigDecimal price = priceService.getPrice(cryptoPair);
        if (price == null || amountToSell.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("No holdings or invalid price to sell for {}", cryptoPair);
            return;
        }
        log.info("walletAmount: {}, amountToSell: {} for cryptoPair: {}", walletAmountDb.getAmount(), amountToSell, cryptoPair);

        BigDecimal amountToUpdate = walletAmountDb.getAmount().subtract(price.multiply(amountToSell)).setScale(8, RoundingMode.HALF_UP);
        WalletAmount updated = walletAmountService.updateWalletAmount(amountToUpdate, walletAmountDb.getId());
        cryptoHoldings.put(cryptoPair, BigDecimal.ZERO);
        log.info("Sold {} of {} at price {}. Updated wallet amount: {}", amountToSell, cryptoPair, price, updated.getAmount());
    }
}
