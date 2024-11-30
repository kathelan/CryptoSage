package pl.kathelan.commons.dto;

import java.time.LocalDateTime;

public class AlertDTO {
    private String cryptoPair;
    private String signalType;
    private LocalDateTime createdAt;

    public AlertDTO() {}

    public AlertDTO(String cryptoPair, String signalType, LocalDateTime createdAt) {
        this.cryptoPair = cryptoPair;
        this.signalType = signalType;
        this.createdAt = createdAt;
    }

    public String getCryptoPair() {
        return cryptoPair;
    }

    public void setCryptoPair(String cryptoPair) {
        this.cryptoPair = cryptoPair;
    }

    public String getSignalType() {
        return signalType;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
