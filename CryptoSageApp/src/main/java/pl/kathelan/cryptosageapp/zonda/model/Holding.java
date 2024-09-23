package pl.kathelan.cryptosageapp.zonda.model;

import jakarta.persistence.*;
import lombok.*;
import pl.kathelan.cryptosageapp.common.model.CommonValues;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

    @Setter
    @Getter
    @Entity(name = "holding")
    @Table(name = "HOLDING")
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class Holding extends CommonValues {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Builder.Default
        @OneToMany(mappedBy = "holding", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        private Set<TransactionHistory> transactionHistories = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_amount_id")
    private WalletAmount walletAmount;

    private BigDecimal changeAmount;

    public Holding(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holding that = (Holding) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
