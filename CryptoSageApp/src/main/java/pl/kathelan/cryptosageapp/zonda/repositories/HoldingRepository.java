package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;

import java.util.Optional;

public interface HoldingRepository extends CrudRepository<Holding, Long> {

    Optional<Holding> findByWalletAmount(WalletAmount walletAmount);
}
