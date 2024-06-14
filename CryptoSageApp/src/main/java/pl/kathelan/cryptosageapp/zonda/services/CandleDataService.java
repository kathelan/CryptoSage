package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleHistoryResponse;
import pl.kathelan.cryptosageapp.zonda.services.client.CandleDataClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandleDataService {

    private final CandleDataClient client;

    public CandleHistoryResponse getHistory(String pair, long from, long to, String resolution) {
        return client.getHistory(pair, from, to, resolution);
    }
}
