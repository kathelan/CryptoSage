package pl.kathelan.cryptosageapp.zonda.services.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.utils.TimeConstants;
import pl.kathelan.cryptosageapp.zonda.services.PriceAggregationService;

import java.time.Duration;

@Service
public class CryptoPriceAggregationScheduler extends AbstractDynamicScheduler {

    private final PriceAggregationService priceAggregationService;

    @Autowired
    public CryptoPriceAggregationScheduler(TaskScheduler taskScheduler, PriceAggregationService priceAggregationService) {
        super(taskScheduler);
        this.priceAggregationService = priceAggregationService;
    }

    @Override
    protected void executeTask() {
        priceAggregationService.aggregateAndCleanOldRecords();
        rescheduleIfNeeded();
    }

    @Override
    protected Duration getInterval() {
        return TimeConstants.ONE_DAY;
    }
}

