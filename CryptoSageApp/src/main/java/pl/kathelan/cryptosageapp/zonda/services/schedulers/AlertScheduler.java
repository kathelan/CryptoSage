package pl.kathelan.cryptosageapp.zonda.services.schedulers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import pl.kathelan.commons.dto.AlertDTO;
import pl.kathelan.cryptosageapp.zonda.mappers.AlertMapper;
import pl.kathelan.cryptosageapp.zonda.model.Alert;
import pl.kathelan.cryptosageapp.zonda.services.AlertService;
import pl.kathelan.notificationmodule.NotificationChannel;

import java.time.Duration;
import java.util.List;

import static pl.kathelan.cryptosageapp.utils.TimeConstants.ONE_MINUTE;

@Component
@ConditionalOnProperty(name = "alerts.scheduler.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class AlertScheduler extends AbstractDynamicScheduler {

    private final AlertService alertService;
    private final NotificationChannel notificationChannel;
    private final AlertMapper alertMapper;

    @Autowired
    public AlertScheduler(TaskScheduler taskScheduler, AlertService alertService, NotificationChannel notificationChannel, AlertMapper alertMapper) {
        super(taskScheduler);
        this.alertService = alertService;
        this.notificationChannel = notificationChannel;
        this.alertMapper = alertMapper;
    }


    @Override
    protected void executeTask() {
        List<Alert> pendingAlerts = alertService.getPendingAlerts();
        log.info("Znaleziono {} alert(ów) w statusie PENDING", pendingAlerts.size());
        for (Alert alert : pendingAlerts) {
            AlertDTO alertDTO = alertMapper.convertToDTO(alert);
            try {
                notificationChannel.sendAlert(alertDTO);
                alertService.updateAlertStatus(alert, Alert.AlertStatus.SENT);
                log.info("Alert ID {} został wysłany i oznaczony jako SENT", alert.getId());
            } catch (Exception e) {
                alertService.updateAlertStatus(alert, Alert.AlertStatus.FAILED);
                log.error("Błąd podczas wysyłania alertu ID {}: {}", alert.getId(), e.getMessage());
            }
        }
    }

    @Override
    protected Duration getInterval() {
        return ONE_MINUTE;
    }
}
