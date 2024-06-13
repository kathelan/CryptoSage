package pl.kathelan.cryptosageapp.zonda.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kathelan.cryptosageapp.zonda.dtos.ticker.TickerResponse;
import pl.kathelan.cryptosageapp.zonda.services.TickerService;


@RestController
@RequestMapping("/tickers")
@RequiredArgsConstructor
@Slf4j
public class TickerController {

    private final TickerService tickerService;

    @GetMapping
    public ResponseEntity<TickerResponse> getTicker() {
        return ResponseEntity.ok(tickerService.getTicketClient());
    }
}
