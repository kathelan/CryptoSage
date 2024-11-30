package pl.kathelan.cryptosageapp.zonda.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.kathelan.cryptosageapp.common.model.CommonValues;
import pl.kathelan.cryptosageapp.zonda.dtos.SignalType;

import java.util.Objects;

@Entity
@Table(name = "signals")
@Getter
@Setter
public class Signal extends CommonValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cryptoPair;
    private String signalType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Signal signal = (Signal) o;
        return Objects.equals(id, signal.id) && Objects.equals(cryptoPair, signal.cryptoPair) && Objects.equals(signalType, signal.signalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, cryptoPair, signalType);
    }
}
