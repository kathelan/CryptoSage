package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kathelan.cryptosageapp.zonda.model.Alert;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.cryptoPair = :cryptoPair AND CAST(a.createdAt AS date) = :date")
    long countByCryptoPairAndCreatedAtDate(
            @Param("cryptoPair") String cryptoPair,
            @Param("date") LocalDate date
    );

    List<Alert> findByStatus(Alert.AlertStatus status);
}