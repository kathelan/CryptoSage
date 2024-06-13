package pl.kathelan.cryptosageapp.zonda.services.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.kathelan.cryptosageapp.zonda.dtos.ticker.TickerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TickerClient {

    private static final String BASE_URL = "https://api.zondacrypto.exchange/rest/trading/ticker/";
    private final WebClient webClient;

    public TickerClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public Mono<TickerResponse> getTickerByTradingPair(String tradingPair) {
        Mono<TickerResponse> tickerResponse = webClient
              .get()
              .uri(tradingPair)
              .header("Accept", "application/json")
              .retrieve()
              .bodyToMono(TickerResponse.class);

      log.info("found ticker response: {}", tickerResponse);
      return tickerResponse;
    }
}
