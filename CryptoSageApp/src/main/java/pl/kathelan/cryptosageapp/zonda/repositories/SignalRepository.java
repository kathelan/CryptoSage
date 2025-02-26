package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.model.Signal;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SignalRepository extends JpaRepository<Signal, Long> {

    List<Signal> findTop5ByCryptoPairOrderByCreatedAtDesc(String cryptoPair);
    @Transactional
    @Modifying
    @Query("DELETE FROM Signal s WHERE s.createdAt < :thresholdDate")
    void deleteByCreatedAtBefore(LocalDateTime thresholdDate);
}
