package pl.kathelan.cryptosageapp.zonda.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.mappers.CryptoCurrencyMapper;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;
import pl.kathelan.cryptosageapp.zonda.repositories.CryptoCurrencyPairRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoCurrencyPairService {
    private final CryptoCurrencyPairRepository pairRepository;
    private final CryptoCurrencyMapper cryptoCurrencyMapper;

    public List<CryptoCurrencyPair> getCryptoCurrencyPairWithRecords(CryptoPair currencyPair) {
        return pairRepository.findAllWithPriceRecordsByPair(currencyPair);
    }

    public List<PriceRecord> getExistingPriceRecords(List<CryptoCurrencyPair> pairs) {
        return pairs.stream()
                .flatMap(cryptoCurrencyPair -> cryptoCurrencyPair.getPriceRecords().stream())
                .collect(Collectors.toList());
    }

    public void createCryptoCurrencyPair(CryptoPair currencyPair, List<Double> priceRecords) {
        CryptoCurrencyPair pair = findCryptoCurrencyByPair(currencyPair);
        Set<PriceRecord> newRecords = cryptoCurrencyMapper.mapPriceRecordsFromDouble(priceRecords, pair);
        pair.getPriceRecords().addAll(newRecords);
        CryptoCurrencyPair saved = pairRepository.save(pair);
        log.debug("Created new currency pair: {}", saved);
    }

    public CryptoCurrencyPair getCryptoCurrencyByPair(CryptoPair pair) {
        return findCryptoCurrencyByPair(pair);
    }

    public List<CryptoCurrencyPair> getCryptoPairs() {
        return pairRepository.findAll();
    }

    private CryptoCurrencyPair findCryptoCurrencyByPair(CryptoPair cryptoPair) {
        return pairRepository.findByCryptoPairWithPriceRecords(cryptoPair).orElseThrow(() -> new EntityNotFoundException("Pair not found"));
    }
}
