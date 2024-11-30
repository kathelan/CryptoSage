package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;

import java.util.List;
import java.util.Optional;


@Repository
public interface CryptoCurrencyPairRepository extends JpaRepository<CryptoCurrencyPair, Long> {

    List<CryptoCurrencyPair> findAllByCryptoPair(CryptoPair cryptoPair);

    Optional<CryptoCurrencyPair> findByCryptoPair(CryptoPair cryptoPair);

    @Query(value = """
                select c from cryptoCurrencyPair c
                join fetch c.priceRecords pr""")
    Optional<CryptoCurrencyPair> findCurrencyPairWithRecordsById(Long id);

    @Query(value = """
                select c from cryptoCurrencyPair c
                join fetch c.priceRecords pr""")
    CryptoCurrencyPair findAllWithPriceRecords();

    @Query(value = """
                select c from cryptoCurrencyPair c
                join fetch c.priceRecords pr
                where c.cryptoPair = :currencyPair
                order by c.createdAt desc
                """)
    List<CryptoCurrencyPair> findAllWithPriceRecordsByPair(CryptoPair currencyPair);

    @Query(value = """
                select c from cryptoCurrencyPair c
                left join fetch c.priceRecords pr
                where c.cryptoPair = :cryptoPair
                """)
    Optional<CryptoCurrencyPair> findByCryptoPairWithPriceRecords(CryptoPair cryptoPair);
}
