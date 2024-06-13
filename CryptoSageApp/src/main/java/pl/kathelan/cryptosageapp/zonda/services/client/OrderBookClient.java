package pl.kathelan.cryptosageapp.zonda.services.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.GenericWebClient;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderBookClient {

    private static final String BASE_URL = "https://api.zondacrypto.exchange/rest/trading/orderbook/";
    private final GenericWebClient webClient;

    public Mono<OrderBookResponse> getOrderBookByTradingPair(String tradingPair) {
        return webClient.get(BASE_URL + tradingPair, OrderBookResponse.class);
    }
}
