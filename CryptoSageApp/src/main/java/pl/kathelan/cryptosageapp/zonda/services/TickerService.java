package pl.kathelan.cryptosageapp.zonda.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.ticker.TickerResponse;
import pl.kathelan.cryptosageapp.zonda.services.client.TickerClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class TickerService {

    private final TickerClient tickerClient;


    public TickerResponse getTicketClient() {
        var abc = tickerClient.getTickerByTradingPair("BTC-PLN").block();
        log.info("kurs btc  do pln na ten moment: {}", abc);
        return abc;
    }
}
