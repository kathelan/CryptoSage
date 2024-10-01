package pl.kathelan.cryptosageapp.zonda.services.schedulers;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Component
public abstract class AbstractDynamicScheduler {
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;
    private Duration currentInterval;
    @Value("${scheduler.enabled}")
    private boolean schedulerEnabled;

    protected AbstractDynamicScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void init() {
        if (schedulerEnabled) {
            scheduleTask();
        }
    }

    protected void scheduleTask() {
        currentInterval = getInterval();
        scheduledFuture = taskScheduler.scheduleAtFixedRate(this::executeTask, currentInterval);
    }

    protected void rescheduleIfNeeded() {
        Duration newInterval = getInterval();
        if (!currentInterval.equals(newInterval)) {
            currentInterval = newInterval;
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            scheduleTask();
        }
    }

    protected abstract void executeTask();

    protected abstract Duration getInterval();
}
