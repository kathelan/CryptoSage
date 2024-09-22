package pl.kathelan.cryptosageapp.zonda.model;

import jakarta.persistence.*;
import lombok.*;
import pl.kathelan.cryptosageapp.common.model.CommonValues;

import java.math.BigDecimal;
import java.util.Objects;

@Setter
@Getter
@Entity(name = "walletAmount")
@Table(name = "WALLET_AMMOUNT")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletAmount extends CommonValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_currency_pair_id", nullable = false)
    private CryptoCurrencyPair cryptoCurrencyPair;

    public WalletAmount(BigDecimal amount) {
        this.amount = amount;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletAmount that = (WalletAmount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
