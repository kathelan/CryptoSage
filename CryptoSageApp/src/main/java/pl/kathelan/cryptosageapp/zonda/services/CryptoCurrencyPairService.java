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

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoCurrencyPairService {
    private final CryptoCurrencyPairRepository pairRepository;
    private final CryptoCurrencyMapper cryptoCurrencyMapper;

    public List<CryptoCurrencyPair> getCryptoCurrencyPair(CryptoPair currencyPair) {
        return pairRepository.findAllByCryptoPair(currencyPair);
    }

    public List<PriceRecord> getPriceRecords(List<CryptoCurrencyPair> cryptoCurrencyPairs) {
        return cryptoCurrencyPairs.stream()
                .flatMap(cryptoCurrencyPair -> cryptoCurrencyPair.getPriceRecords().stream())
                .toList();
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

    private CryptoCurrencyPair findCryptoCurrencyByPair(CryptoPair cryptoPair) {
        return pairRepository.findByCryptoPair(cryptoPair).orElseThrow(() -> new EntityNotFoundException("Pair not found"));
    }
}
