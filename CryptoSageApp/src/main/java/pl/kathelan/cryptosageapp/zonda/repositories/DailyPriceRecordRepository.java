package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kathelan.cryptosageapp.zonda.model.DailyPriceRecord;

@Repository
public interface DailyPriceRecordRepository extends JpaRepository<DailyPriceRecord, Long> {
}
