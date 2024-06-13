package pl.kathelan.cryptosageapp.zonda.services.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.GenericWebClient;
import pl.kathelan.cryptosageapp.zonda.dtos.ticker.TickerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class TickerClient {

    private static final String BASE_URL = "https://api.zondacrypto.exchange/rest/trading/ticker/";
    private final GenericWebClient webClient;

    public Mono<TickerResponse> getTickerByTradingPair(String tradingPair) {
        return webClient.get(BASE_URL + tradingPair, TickerResponse.class);
    }
}
