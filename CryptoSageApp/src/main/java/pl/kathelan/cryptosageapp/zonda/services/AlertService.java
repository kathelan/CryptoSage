package pl.kathelan.cryptosageapp.zonda.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.commons.dto.AlertDTO;
import pl.kathelan.cryptosageapp.zonda.model.Alert;
import pl.kathelan.cryptosageapp.zonda.repositories.AlertRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    @Autowired
    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional
    public void createAlert(String cryptoPair, String signalType) {
        long dailyAlertCount = alertRepository.countByCryptoPairAndCreatedAtDate(cryptoPair, LocalDate.now());

        if (dailyAlertCount >= 3) {
            return;
        }

        Alert alert = new Alert();
        alert.setCryptoPair(cryptoPair);
        alert.setSignalType(signalType);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setStatus(Alert.AlertStatus.PENDING);

        alertRepository.save(alert);
    }

    @Transactional
    public void updateAlertStatus(Alert alert, Alert.AlertStatus status) {
        alert.setStatus(status);
        alertRepository.save(alert);
    }

    public List<Alert> getPendingAlerts() {
        return alertRepository.findByStatus(Alert.AlertStatus.PENDING);
    }
}