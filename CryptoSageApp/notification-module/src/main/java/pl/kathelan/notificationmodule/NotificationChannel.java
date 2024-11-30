package pl.kathelan.notificationmodule;

import pl.kathelan.commons.dto.AlertDTO;

public interface NotificationChannel {
    void sendAlert(AlertDTO alertDTO) throws Exception;
}
