package pl.kathelan.cryptosageapp.zonda.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.kathelan.cryptosageapp.common.model.CommonValues;
import pl.kathelan.cryptosageapp.zonda.dtos.SignalType;

import java.util.Objects;

@Entity
@Table(name = "alerts")
@Getter
@Setter
public class Alert extends CommonValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cryptoPair;
    private String signalType;

    @Enumerated(EnumType.STRING)
    private AlertStatus status;


    public enum AlertStatus {
        PENDING,
        SENT,
        FAILED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Alert alert = (Alert) o;
        return Objects.equals(id, alert.id) && Objects.equals(cryptoPair, alert.cryptoPair) && Objects.equals(signalType, alert.signalType) && status == alert.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, cryptoPair, signalType, status);
    }
}
