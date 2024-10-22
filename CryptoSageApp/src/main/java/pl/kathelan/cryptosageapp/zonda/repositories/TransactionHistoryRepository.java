package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kathelan.cryptosageapp.zonda.model.TransactionHistory;

import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    Optional<TransactionHistory> findFirstByHoldingIdOrderByIdDesc(Long holdingId);
}
