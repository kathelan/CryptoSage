package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import pl.kathelan.cryptosageapp.zonda.services.OrderBookService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {
    private final OrderBookService orderBookService;

    public BigDecimal getPrice(CryptoPair cryptoPair) {
        double price = getCurrentPrice(cryptoPair);
        return price <= 0 ? null : BigDecimal.valueOf(price);
    }

    private double getCurrentPrice(CryptoPair cryptoPair) {
        OrderBookResponse orderBookResponse = getOrderBookResponseByTradingPair(cryptoPair.getValue());
        if (orderBookResponse == null || !"Ok".equals(orderBookResponse.getStatus()) || orderBookResponse.getBuy().isEmpty() || orderBookResponse.getSell().isEmpty()) {
            return 0.0;
        }

        double highestBid = orderBookResponse.getBuy().stream()
                .mapToDouble(buy -> Double.parseDouble(buy.getRa()))
                .max().orElse(0.0);
        double lowestAsk = orderBookResponse.getSell().stream()
                .mapToDouble(sell -> Double.parseDouble(sell.getRa()))
                .min().orElse(0.0);

        return (highestBid + lowestAsk) / 2;
    }

    private OrderBookResponse getOrderBookResponseByTradingPair(String tradingPair) {
        return orderBookService.getOrderBookResponseByTradingPair(tradingPair);
    }
}
