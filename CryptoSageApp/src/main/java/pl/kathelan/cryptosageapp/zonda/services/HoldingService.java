package pl.kathelan.cryptosageapp.zonda.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.repositories.HoldingRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class HoldingService {
    private final HoldingRepository holdingRepository;

    public void initHoldingsAmount(WalletAmount walletAmount, BigDecimal initAmount) {
        log.info("Initialized holdings wallet for {} for pair: {}", walletAmount, walletAmount.getCryptoCurrencyPair());
        createHoldingsAmount(walletAmount, initAmount);
    }

    private void createHoldingsAmount(WalletAmount walletAmount, BigDecimal initAmount) {
        Holding holding = Holding.builder()
                .walletAmount(walletAmount)
                .changeAmount(initAmount)
                .build();

        Holding holdingToSave = holdingRepository.save(holding);
        log.info("Holding saved for wallet pair: {}, amount: {}", holdingToSave.getWalletAmount().getCryptoCurrencyPair(), holdingToSave.getChangeAmount());
    }

    public Holding getHoldingByWalletAmount(WalletAmount walletAmountDb) {
        return getByWalletAmount(walletAmountDb);
    }

    public void updateHolding(BigDecimal amountToUpdate, Long holdingId) {
        Holding holding = holdingRepository.findById(holdingId).orElseThrow(() -> new EntityNotFoundException("Holding now found with id: " + holdingId));
        holding.setChangeAmount(holding.getChangeAmount().add(amountToUpdate));
        Holding updated = holdingRepository.save(holding);
        log.info("Updated holding for pair: {}, amount: {}", updated.getWalletAmount().getCryptoCurrencyPair(), updated.getChangeAmount());
    }

    public Map<CryptoPair, BigDecimal> getHoldings() {
        return StreamSupport.stream(findHoldings().spliterator(), false)
                .collect(Collectors.toMap(
                        holdings -> holdings.getWalletAmount().getCryptoCurrencyPair().getCryptoPair(),
                        Holding::getChangeAmount,
                        (existing, replacement) -> existing));
    }

    private Iterable<Holding> findHoldings() {
        return holdingRepository.findAll();
    }

    private Holding getByWalletAmount(WalletAmount walletAmount) {
        return holdingRepository.findByWalletAmount(walletAmount).orElseThrow(() -> new EntityNotFoundException("Not found holding by walletAmount: " + walletAmount));
    }
}
