package pl.kathelan.cryptosageapp.zonda.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import pl.kathelan.cryptosageapp.common.model.CommonValues;

import java.math.BigDecimal;
import java.util.Objects;

@Setter
@Getter
@Entity(name = "transactionHistory")
@Table(name = "TRANSACTION_HISTORY")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory extends CommonValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holding_id")
    private Holding holding;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    private BigDecimal quantity;

    private BigDecimal pricePerUnit;

    @Formula("quantity * price_per_unit")
    private BigDecimal totalValue;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionHistory that = (TransactionHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
