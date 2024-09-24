package pl.kathelan.cryptosageapp.zonda.services.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.services.calculation.MACDService;

import java.util.Arrays;

@ConditionalOnProperty(
        value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
)
@Component
@RequiredArgsConstructor
public class MACDScheduler {

    private final MACDService macdService;

    @Scheduled(fixedRate = 300000)
    void scheduleHistoricalPrices() {
        Arrays.stream(CryptoPair.values()).forEach(macdService::analyzeMarket);
    }
}
