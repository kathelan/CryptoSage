package pl.kathelan.cryptosageapp.zonda.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.TransactionHistory;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.repositories.HoldingRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
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

    @Transactional
    public void updateHolding(BigDecimal amountToUpdate, Long holdingId, TransactionHistory transactionHistory) {
        Holding holding = holdingRepository.findByIdWithTransactions(holdingId).orElseThrow(() -> new EntityNotFoundException("Holding now found with id: " + holdingId));
        if (amountToUpdate.equals(BigDecimal.ZERO)) {
            holding.setChangeAmount(BigDecimal.ZERO);
        } else {
            holding.setChangeAmount(holding.getChangeAmount().add(amountToUpdate));
        }
        holding.getTransactionHistories().add(transactionHistory);
        Holding updated = holdingRepository.save(holding);
        log.info("Updated holding for pair: {}, amount: {}", updated.getWalletAmount().getCryptoCurrencyPair(), updated.getChangeAmount());
        if (transactionHistory != null) {
            log.info("Saved Transaction for type: {}, amount: {}, pricePerUnit: {}", transactionHistory.getTransactionType(), transactionHistory.getQuantity(), transactionHistory.getPricePerUnit());
        }
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
