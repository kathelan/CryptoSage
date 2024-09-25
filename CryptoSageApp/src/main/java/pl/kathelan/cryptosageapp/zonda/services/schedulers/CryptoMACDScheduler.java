package pl.kathelan.cryptosageapp.zonda.services.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.utils.TimeConstants;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.services.PriceRecordService;
import pl.kathelan.cryptosageapp.zonda.services.calculation.MACDService;

import java.time.Duration;
import java.util.Arrays;

@Service
public class CryptoMACDScheduler extends AbstractDynamicScheduler {
    private final MACDService macdService;
    private final PriceRecordService priceRecordService;

    @Autowired
    public CryptoMACDScheduler(TaskScheduler taskScheduler, MACDService macdService, PriceRecordService priceRecordService) {
        super(taskScheduler);
        this.macdService = macdService;
        this.priceRecordService = priceRecordService;
    }

    @Override
    protected void executeTask() {
        Arrays.stream(CryptoPair.values())
                .parallel()
                .forEach(macdService::analyzeMarket);

        rescheduleIfNeeded();
    }

    @Override
    protected Duration getInterval() {
        boolean isWeekPassed = Arrays.stream(CryptoPair.values())
                .allMatch(this::isWeekPassedForCryptoPair);

        return isWeekPassed ? TimeConstants.ONE_HOUR : TimeConstants.FIVE_MINUTES;
    }

    private boolean isWeekPassedForCryptoPair(CryptoPair cryptoPair) {
        Long startTime = priceRecordService.getStartTime(cryptoPair);
        long timeSinceStart = System.currentTimeMillis() - startTime;
        return timeSinceStart >= TimeConstants.ONE_WEEK;
    }
}
