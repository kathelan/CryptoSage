package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;

import java.util.Optional;

@Repository
public interface WalletAmountRepository extends CrudRepository<WalletAmount, Long> {

    Optional<WalletAmount> findByCryptoCurrencyPair(CryptoCurrencyPair cryptoCurrencyPair);

}
