package pl.kathelan.cryptosageapp.zonda.services.schedulers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.utils.TimeConstants;
import pl.kathelan.cryptosageapp.zonda.services.SignalService;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(name = "signals.scheduler.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class SignalCleanupScheduler extends AbstractDynamicScheduler {

    private final SignalService signalService;


    public SignalCleanupScheduler(TaskScheduler taskScheduler, SignalService signalService) {
        super(taskScheduler);
        this.signalService = signalService;
    }


    @Override
    protected void executeTask() {
        log.info("Usuwanie starych sygnałów");
        signalService.deleteByCreatedAtBefore(LocalDateTime.now().minusDays(30));
    }

    @Override
    protected Duration getInterval() {
        return TimeConstants.ONE_DAY;
    }
}
