package pl.kathelan.cryptosageapp.zonda.services.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.GenericWebClient;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import pl.kathelan.cryptosageapp.zonda.dtos.trades.LastTradesResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class LastTradesClient {

    private static final String BASE_URL = "https://api.zondacrypto.exchange/rest/trading/transactions";
    private final GenericWebClient webClient;

    public Mono<LastTradesResponse> getLastTradesPerTradingPair(String tradingPair) {
        return webClient.get(BASE_URL + tradingPair, LastTradesResponse.class);
    }
}
