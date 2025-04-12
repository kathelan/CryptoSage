package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceRecordRepository extends JpaRepository<PriceRecord, Long> {

    @Query("SELECT MIN(pr.createdAt) FROM PriceRecord pr WHERE pr.cryptoCurrencyPair.cryptoPair = :cryptoPair")
    LocalDateTime findEarliestCreatedAtByCryptoPair(@Param("cryptoPair") CryptoPair cryptoPair);

    /**
     * Usuwa rekordy starsze niż określona liczba dni.
     *
     * @param thresholdDate liczba dni (np. starsze niż 30 dni)
     * @return liczba usuniętych rekordów
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PriceRecord p WHERE p.createdAt < :thresholdDate")
    int deleteOldRecords(@Param("thresholdDate") LocalDateTime thresholdDate);

    /**
     * Agreguje średnie ceny dla określonej pary kryptowalut starsze niż podana liczba dni.
     * Zwraca średnią cenę dla każdego dnia.
     *
     * @param cryptoPair para kryptowalut
     * @param thresholdDate       liczba dni (np. starsze niż 30 dni)
     * @return lista zagregowanych danych: [data, średnia cena]
     */
    @Query("SELECT CAST(p.createdAt AS LocalDate), AVG(p.price) " +
            "FROM PriceRecord p " +
            "WHERE p.cryptoCurrencyPair = :cryptoPair " +
            "AND p.createdAt < :thresholdDate " +
            "GROUP BY CAST(p.createdAt AS LocalDate)")
    List<Object[]> aggregatePriceData(@Param("cryptoPair") CryptoCurrencyPair cryptoPair, @Param("thresholdDate") LocalDateTime thresholdDate);

    /**
     * Wyszukuje rekordy cenowe z zastosowaniem opcjonalnych filtrów.
     *
     * Metoda wykonuje zapytanie do bazy danych, które pobiera rekordy cenowe kryptowalut
     * z uwzględnieniem podanych filtrów. Wykorzystuje JOIN FETCH aby zapobiec problemowi N+1
     * podczas ładowania powiązanych par kryptowalut.
     *
     * @param pair          Para kryptowalut do filtrowania (opcjonalne - null oznacza brak filtra)
     * @param startDateTime Data początkowa zakresu czasowego (opcjonalne - null oznacza brak dolnej granicy)
     * @param endDateTime   Data końcowa zakresu czasowego (opcjonalne - null oznacza brak górnej granicy)
     * @param pageable      Obiekt definiujący parametry paginacji (numer strony, rozmiar strony, sortowanie)
     * @return              Stronicowany zbiór rekordów cenowych spełniających podane kryteria
     */
    @Query("SELECT pr FROM PriceRecord pr " +
            "JOIN FETCH pr.cryptoCurrencyPair ccp " +
            "WHERE (:pair IS NULL OR ccp.cryptoPair = :pair) " +
            "AND (:startDateTime IS NULL OR pr.createdAt >= :startDateTime) " +
            "AND (:endDateTime IS NULL OR pr.createdAt <= :endDateTime)")
    Page<PriceRecord> findWithFilters(
            @Param("pair") CryptoPair pair,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );
}
