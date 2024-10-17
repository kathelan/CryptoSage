package pl.kathelan.cryptosageapp.zonda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kathelan.cryptosageapp.common.model.CommonValues;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "PRICE_RECORD_DAILY")
@Setter
@Getter
@NoArgsConstructor
public class DailyPriceRecord extends CommonValues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double avgPrice;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_currency_pair_id", nullable = false)
    private CryptoCurrencyPair cryptoCurrencyPair;

    public DailyPriceRecord(Double avgPrice, CryptoCurrencyPair cryptoCurrencyPair, LocalDate date) {
        this.avgPrice = avgPrice;
        this.cryptoCurrencyPair = cryptoCurrencyPair;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyPriceRecord dailyPriceRecord = (DailyPriceRecord) o;

        if (id != null && dailyPriceRecord.id != null) {
            return Objects.equals(id, dailyPriceRecord.id);
        }
        return Objects.equals(avgPrice, dailyPriceRecord.avgPrice);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(avgPrice);
    }
}

