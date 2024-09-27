package pl.kathelan.cryptosageapp.zonda.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;

import java.util.Optional;

@Repository
public interface HoldingRepository extends CrudRepository<Holding, Long> {

    Optional<Holding> findByWalletAmount(WalletAmount walletAmount);

    @Query(value = """
            select h from holding h
            left join fetch h.transactionHistories
            where h.id = :holdingId
            """)
    Optional<Holding> findByIdWithTransactions(Long holdingId);
}
