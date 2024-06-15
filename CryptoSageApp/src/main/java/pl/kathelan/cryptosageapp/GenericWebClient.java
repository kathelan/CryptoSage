package pl.kathelan.cryptosageapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GenericWebClient {

    private final WebClient webClient;

    public GenericWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public <T> Mono<T> get(String url, Class<T> responseType) {
        return webClient
                .get()
                .uri(url)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> post(String url, Object requestBody, Class<T> responseType) {
        return webClient
                .post()
                .uri(url)
                .header("Content-Type", "application/json")
                .body(Mono.just(requestBody), requestBody.getClass())
                .retrieve()
                .bodyToMono(responseType);
    }
}
