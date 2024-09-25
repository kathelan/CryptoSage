package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;

import java.time.LocalDateTime;

@Repository
public interface PriceRecordRepository extends JpaRepository<PriceRecord, Long> {

    @Query("SELECT MIN(pr.createdAt) FROM PriceRecord pr WHERE pr.cryptoCurrencyPair.cryptoPair = :cryptoPair")
    LocalDateTime findEarliestCreatedAtByCryptoPair(@Param("cryptoPair") CryptoPair cryptoPair);
}
