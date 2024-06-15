package pl.kathelan.cryptosageapp.zonda.services.calculation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import pl.kathelan.cryptosageapp.zonda.services.OrderBookService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletOperationService {

    @Getter
    private final Map<CryptoPair, Double> walletAmounts = new EnumMap<>(CryptoPair.class);
    @Getter
    private final Map<CryptoPair, Double> cryptoHoldings = new EnumMap<>(CryptoPair.class);
    private final Set<CryptoPair> initializedPairs = new HashSet<>(); // Zbiór zainicjalizowanych par
    private final OrderBookService orderBookService;

    public void performOperations(Map<CryptoPair, Signal> signalMap) {
        initializeWallets();
        for (Map.Entry<CryptoPair, Signal> entry : signalMap.entrySet()) {
            CryptoPair cryptoPair = entry.getKey();
            Signal signal = entry.getValue();
            try {
                switch (signal) {
                    case BUY -> buyCrypto(cryptoPair);
                    case SELL -> sellCrypto(cryptoPair);
                    case HOLD -> log.info("Holding position for {}", cryptoPair);
                    default -> log.warn("Unknown signal for {}: {}", cryptoPair, signal);
                }
            } catch (Exception e) {
                log.error("Error performing operation for {}: {}", cryptoPair, e.getMessage(), e);
            }
        }
    }

    private void initializeWallets() {
        for (CryptoPair cryptoPair : CryptoPair.values()) {
            if (!initializedPairs.contains(cryptoPair)) {
                log.info("Initialized wallet for {}", cryptoPair);
                walletAmounts.put(cryptoPair, 300.0); // Ustaw początkowe środki tylko raz
                log.info("created wallet for {} with value: {}", cryptoPair, walletAmounts.get(cryptoPair));
                cryptoHoldings.put(cryptoPair, 0.0); // Inicjalizuj posiadania na 0
                log.info("created holdings for {} with value: {}", cryptoPair, cryptoHoldings.get(cryptoPair));
                initializedPairs.add(cryptoPair); // Dodaj parę do zbioru zainicjalizowanych par
            }
        }
    }

    private synchronized void buyCrypto(CryptoPair cryptoPair) {
        log.info("Starting buying crypto for pair: {}, current wallet value: {} and holdings: {}", cryptoPair, walletAmounts.get(cryptoPair), cryptoHoldings.get(cryptoPair));
        double walletAmount = walletAmounts.getOrDefault(cryptoPair, 0.0);

        if (walletAmount <= 0) {
            log.warn("No funds available to buy {}", cryptoPair);
            return;
        }

        Double price = getPrice(cryptoPair);
        if (price == null) return;

        double amountToBuy = walletAmount / price;
        amountToBuy = round(amountToBuy, 8);

        log.info("walletAmount: {}, amountToBuy:{} for cryptoPair: {}", walletAmount, amountToBuy, cryptoPair);

        walletAmounts.put(cryptoPair, round(walletAmount - price * amountToBuy, 8));
        cryptoHoldings.put(cryptoPair, round(cryptoHoldings.getOrDefault(cryptoPair, 0.0) + amountToBuy, 8));
        log.info("Bought {} of {} at price {}. Remaining wallet amount: {}", amountToBuy, cryptoPair, price, walletAmounts.get(cryptoPair));
    }

    private synchronized void sellCrypto(CryptoPair cryptoPair) {
        log.info("Starting selling crypto for pair: {}, current wallet value: {} and holdings: {}", cryptoPair, walletAmounts.get(cryptoPair), cryptoHoldings.get(cryptoPair));
        double amountToSell = cryptoHoldings.getOrDefault(cryptoPair, 0.0);

        if (amountToSell <= 0) {
            log.warn("No holdings to sell for {}", cryptoPair);
            return;
        }

        amountToSell = round(amountToSell, 8);

        Double price = getPrice(cryptoPair);
        if (price == null) return;

        log.info("walletAmount: {}, amountToSell: {} for cryptoPair: {}", walletAmounts.get(cryptoPair), amountToSell, cryptoPair);

        walletAmounts.put(cryptoPair, round(walletAmounts.get(cryptoPair) + price * amountToSell, 8));
        cryptoHoldings.put(cryptoPair, 0.0);
        log.info("Sold {} of {} at price {}. Updated wallet amount: {}", amountToSell, cryptoPair, price, walletAmounts.get(cryptoPair));
    }

    private Double getPrice(CryptoPair cryptoPair) {
        double price = getCurrentPrice(cryptoPair);

        if (price <= 0) {
            log.warn("Invalid price for {}: {}", cryptoPair, price);
            return null;
        }
        return price;
    }

    private double getCurrentPrice(CryptoPair cryptoPair) {
        OrderBookResponse orderBookResponse = getOrderBookResponseByTradingPair(cryptoPair.getValue());
        if (orderBookResponse == null) {
            log.warn("No response for trading pair: {}", cryptoPair);
            return 0.0;
        }
        if (!"Ok".equals(orderBookResponse.getStatus())) {
            log.warn("Invalid status for order book response: {}", orderBookResponse.getStatus());
            return 0.0;
        }
        if (orderBookResponse.getBuy().isEmpty() || orderBookResponse.getSell().isEmpty()) {
            log.warn("Empty order book for trading pair: {}", cryptoPair);
            return 0.0;
        }

        double highestBid = orderBookResponse.getBuy().stream()
                .mapToDouble(buy -> Double.parseDouble(buy.getRa()))
                .max()
                .orElse(0.0);
        double lowestAsk = orderBookResponse.getSell().stream()
                .mapToDouble(sell -> Double.parseDouble(sell.getRa()))
                .min()
                .orElse(0.0);

        return (highestBid + lowestAsk) / 2; // Średnia z najwyższej oferty kupna i najniższej oferty sprzedaży
    }

    private OrderBookResponse getOrderBookResponseByTradingPair(String tradingPair) {
        return orderBookService.getOrderBookResponseByTradingPair(tradingPair);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
