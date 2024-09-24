package pl.kathelan.cryptosageapp.zonda.model;

import jakarta.persistence.*;
import lombok.*;
import pl.kathelan.cryptosageapp.common.model.CommonValues;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Entity(name = "cryptoCurrencyPair")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CRYPTO_CURRENCY_PAIR")
public class CryptoCurrencyPair extends CommonValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CryptoPair cryptoPair;

    @OneToMany(mappedBy = "cryptoCurrencyPair", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PriceRecord> priceRecords = new HashSet<>();

    public void add(PriceRecord priceRecord) {
        if (priceRecord != null) {
            if (priceRecords == null) {
                priceRecords = new HashSet<>();
            }
            priceRecords.add(priceRecord);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoCurrencyPair that = (CryptoCurrencyPair) o;
        return Objects.equals(id, that.id) && cryptoPair == that.cryptoPair;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cryptoPair);
    }

    @Override
    public String toString() {
        return "CryptoCurrencyPair{" +
                "id=" + id +
                ", cryptoPair=" + cryptoPair +
                '}';
    }
}
