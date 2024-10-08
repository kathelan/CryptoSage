package pl.kathelan.cryptosageapp.zonda.services.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleHistoryResponse;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleHistoryResponseDeserializer;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CandleDataClient {

    private static final String BASE_URL = "https://api.zondacrypto.exchange/rest/trading/candle/history/";

    public CandleHistoryResponse getHistory(String tradingPair, long from, long to, String resolution) {
        WebClient client = WebClient.builder().baseUrl(BASE_URL).build();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("from", String.valueOf(from));
        queryParams.put("to", String.valueOf(to));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(queryParams);
        var responseJson = client.
                get()
                .uri(uriBuilder -> uriBuilder.path(tradingPair + "/" + resolution).queryParams(params).build())
                .retrieve()
                .bodyToMono(String.class).block();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CandleHistoryResponse.class, new CandleHistoryResponseDeserializer());
        mapper.registerModule(module);

        CandleHistoryResponse response = null;
        try {
            response = mapper.readValue(responseJson, CandleHistoryResponse.class);
            return response;
        } catch (JsonProcessingException e) {
            return null;
        }
    }


}
