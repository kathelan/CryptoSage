package pl.kathelan.cryptosageapp.zonda.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.kathelan.cryptosageapp.common.model.CommonValues;

import java.util.Objects;

@Setter
@Getter
@Entity(name = "priceRecord")
@Table(name = "PRICE_RECORD")
public class PriceRecord extends CommonValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_currency_pair_id", nullable = false)
    private CryptoCurrencyPair cryptoCurrencyPair;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceRecord that = (PriceRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
