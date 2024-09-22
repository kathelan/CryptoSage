package pl.kathelan.cryptosageapp.zonda.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.WalletAmount;
import pl.kathelan.cryptosageapp.zonda.repositories.WalletAmountRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Slf4j
@Service
public class WalletAmountService {
    private final WalletAmountRepository walletAmountRepository;
    private final CryptoCurrencyPairService cryptoCurrencyPairService;

    public void initWalletAmount(CryptoPair cryptoPair) {
        log.info("Initialized wallet for {}", cryptoPair);
        BigDecimal initAmount = new BigDecimal("300.0");
        CryptoCurrencyPair cryptoCurrencyPair = cryptoCurrencyPairService.getCryptoCurrencyByPair(cryptoPair);
        createWalletAmount(cryptoCurrencyPair, initAmount);
        log.info("created wallet for {} with value: {}", cryptoPair, initAmount);
    }

    public Set<CryptoPair> getInitializedCryptoPairs() {
        Iterable<WalletAmount> walletAmounts = findWalletAmounts();
        return StreamSupport.stream(walletAmounts.spliterator(), false)
                .map(WalletAmount::getCryptoCurrencyPair)
                .map(CryptoCurrencyPair::getCryptoPair)
                .collect(Collectors.toSet());
    }

    private Iterable<WalletAmount> findWalletAmounts() {
        return walletAmountRepository.findAll();
    }

    public WalletAmount getWalletAmountByCryptoPair(CryptoPair cryptoPair) {
        CryptoCurrencyPair cryptoCurrencyPair = cryptoCurrencyPairService.getCryptoCurrencyByPair(cryptoPair);
        return findByCryptoPair(cryptoCurrencyPair);
    }

    public WalletAmount createWalletAmount(CryptoCurrencyPair cryptoCurrencyPair, BigDecimal amount) {
        WalletAmount walletAmount = WalletAmount.builder()
                .amount(amount)
                .cryptoCurrencyPair(cryptoCurrencyPair)
                .build();
        return saveAndLogWalletAmount(walletAmount, "Saved");
    }

    public WalletAmount updateWalletAmount(BigDecimal amount, Long walletId) {
        WalletAmount walletAmount = getWalletAmountById(walletId);
        walletAmount.setAmount(amount);
        return saveAndLogWalletAmount(walletAmount, "Updated");
    }

    private WalletAmount saveAndLogWalletAmount(WalletAmount walletAmount, String operation) {
        WalletAmount saved = walletAmountRepository.save(walletAmount);
        log.info("{} walletAmount for pair: {} and amount: {}",
                operation, saved.getCryptoCurrencyPair().getCryptoPair(), saved.getAmount());
        return saved;
    }

    private WalletAmount getWalletAmountById(Long walletAmountId) {
        return walletAmountRepository.findById(walletAmountId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find wallet amount with ID: " + walletAmountId));
    }

    private WalletAmount findByCryptoPair(CryptoCurrencyPair cryptoCurrencyPair) {
        return walletAmountRepository.findByCryptoCurrencyPair(cryptoCurrencyPair)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find wallet amount for pair: " + cryptoCurrencyPair.getCryptoPair()));
    }

    public Map<CryptoPair, BigDecimal> getWallets() {
        return StreamSupport.stream(findWalletAmounts().spliterator(), false)
                .collect(Collectors.toMap(
                        walletAmount -> walletAmount.getCryptoCurrencyPair().getCryptoPair(),
                        WalletAmount::getAmount,
                        (existing, replacement) -> existing));
    }
}
