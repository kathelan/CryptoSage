package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;

import java.util.List;
import java.util.Optional;


public interface CryptoCurrencyPairRepository extends CrudRepository<CryptoCurrencyPair, Long> {

    List<CryptoCurrencyPair> findAllByCryptoPair(CryptoPair cryptoPair);

    Optional<CryptoCurrencyPair> findByCryptoPair(CryptoPair cryptoPair);
}
