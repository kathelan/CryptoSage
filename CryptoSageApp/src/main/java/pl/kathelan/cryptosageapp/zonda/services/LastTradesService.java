package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import pl.kathelan.cryptosageapp.zonda.dtos.trades.LastTradesResponse;
import pl.kathelan.cryptosageapp.zonda.services.client.LastTradesClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastTradesService {

    private final LastTradesClient lastTradesClient;

    /**
     * Historia transakcji
     * @return Shows the list of most recent transactions for given market. By default returns list of 10 most recent transactions.
     */
    public LastTradesResponse getOrderBookResponseByTradingPair(String tradingPair) {
        LastTradesResponse lastTradesResponse = lastTradesClient.getLastTradesPerTradingPair(tradingPair).block();
        log.info("Shows the list of most recent transactions for given market: {} with obj: {}", tradingPair, lastTradesResponse);
        return lastTradesResponse;
    }
}
