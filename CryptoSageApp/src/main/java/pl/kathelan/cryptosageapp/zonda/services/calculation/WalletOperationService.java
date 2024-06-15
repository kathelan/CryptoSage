package pl.kathelan.cryptosageapp.zonda.services.calculation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import pl.kathelan.cryptosageapp.zonda.services.OrderBookService;

import java.util.EnumMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletOperationService {

    private final Map<CryptoPair, Double> walletAmounts = new EnumMap<>(CryptoPair.class);
    private final Map<CryptoPair, Double> cryptoHoldings = new EnumMap<>(CryptoPair.class);
    private final OrderBookService orderBookService;


    public void performOperations(Map<CryptoPair, Signal> signalMap) {
        for (CryptoPair cryptoPair : CryptoPair.values()) {
            walletAmounts.putIfAbsent(cryptoPair, 300.0);
        }
        for (Map.Entry<CryptoPair, Signal> entry : signalMap.entrySet()) {
            CryptoPair cryptoPair = entry.getKey();
            Signal signal = entry.getValue();
            try {
                switch (signal) {
                    case BUY:
                        buyCrypto(cryptoPair);
                        break;
                    case SELL:
                        sellCrypto(cryptoPair);
                        break;
                    case HOLD:
                        log.info("Holding position for {}", cryptoPair);
                        break;
                    default:
                        log.warn("Unknown signal for {}: {}", cryptoPair, signal);
                }
            } catch (Exception e) {
                log.error("Error performing operation for {}: {}", cryptoPair, e.getMessage());
            }
        }
    }

    private void buyCrypto(CryptoPair cryptoPair) {
        double price = getCurrentPrice(cryptoPair);
        double walletAmount = walletAmounts.getOrDefault(cryptoPair, 0.0);
        double amountToBuy = walletAmount / price;
        log.info("walletAmount: {}, amountToBuy:{} for cryptoPair: {}", walletAmount, amountToBuy, cryptoPair);
        if (walletAmount >= price * amountToBuy) {
            walletAmount -= price * amountToBuy;
            cryptoHoldings.put(cryptoPair, cryptoHoldings.getOrDefault(cryptoPair, 0.0) + amountToBuy);
            log.info("Bought {} of {} at price {}. Remaining wallet amount: {}", amountToBuy, cryptoPair, price, walletAmount);
        } else {
            log.warn("Not enough funds to buy {}", cryptoPair);
        }
    }

    private void sellCrypto(CryptoPair cryptoPair) {
        double price = getCurrentPrice(cryptoPair);
        double amountToSell = cryptoHoldings.getOrDefault(cryptoPair, 0.0);
        double walletAmount = walletAmounts.getOrDefault(cryptoPair, 0.0);
        log.info("walletAmount: {}, amountToSell: {} for cryptoPair: {}", walletAmount, amountToSell, cryptoPair);
        if (amountToSell > 0) {
            walletAmount += price * amountToSell;
            cryptoHoldings.put(cryptoPair, 0.0);
            log.info("Sold {} of {} at price {}. Updated wallet amount: {}", amountToSell, cryptoPair, price, walletAmount);
        } else {
            log.warn("No holdings to sell for {}", cryptoPair);
        }
    }

    private double getCurrentPrice(CryptoPair cryptoPair) {
        OrderBookResponse orderBookResponse = getOrderBookResponseByTradingPair(cryptoPair.getValue());
        if (orderBookResponse != null && orderBookResponse.status.equals("Ok")) {
            double highestBid = orderBookResponse.buy.stream()
                    .mapToDouble(buy -> Double.parseDouble(buy.ra))
                    .max()
                    .orElse(0.0);
            double lowestAsk = orderBookResponse.sell.stream()
                    .mapToDouble(sell -> Double.parseDouble(sell.ra))
                    .min()
                    .orElse(0.0);
            return (highestBid + lowestAsk) / 2; // Średnia z najwyższej oferty kupna i najniższej oferty sprzedaży
        }
        return 0.0;
    }

    private OrderBookResponse getOrderBookResponseByTradingPair(String tradingPair) {
        return orderBookService.getOrderBookResponseByTradingPair(tradingPair);
    }
}