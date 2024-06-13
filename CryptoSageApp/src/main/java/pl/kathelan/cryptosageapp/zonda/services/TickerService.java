package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.ticker.TickerResponse;
import pl.kathelan.cryptosageapp.zonda.services.client.TickerClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class TickerService {

    private final TickerClient tickerClient;

    /**
     *  Ticker: Aktualne dane rynkowe (cena, wolumen, itd.)
     * @param tradingPair
     * @return
     */
    public TickerResponse getTicketClient(String tradingPair) {
        TickerResponse ticketResponse = tickerClient.getTickerByTradingPair(tradingPair).block();
        log.info("kurs btc  do pln na ten moment: {}", ticketResponse);
        return ticketResponse;
    }
}
