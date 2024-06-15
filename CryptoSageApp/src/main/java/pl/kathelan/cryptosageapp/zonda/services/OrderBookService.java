package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import pl.kathelan.cryptosageapp.zonda.services.client.OrderBookClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderBookService {

    private final OrderBookClient orderBookClient;

    /**
     * Księga zleceń: Aktualne zlecenia kupna i sprzedaży
     * @return Returns 300 of highest bid orders and 300 of lowest ask orders.
     */
    public OrderBookResponse getOrderBookResponseByTradingPair(String tradingPair) {
        OrderBookResponse orderBookResponse = orderBookClient.getOrderBookByTradingPair(tradingPair).block();
        log.info("Sells and buys for coin: {}", tradingPair);
        return orderBookResponse;
    }
}
