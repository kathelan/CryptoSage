package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.repositories.PriceRecordRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceRecordService {
    private final PriceRecordRepository priceRecordRepository;
    private final CryptoCurrencyPairService cryptoCurrencyPairService;

    public Long getStartTime(CryptoPair cryptoPair) {
        LocalDateTime earliestCreatedAt = priceRecordRepository.findEarliestCreatedAtByCryptoPair(cryptoPair);
        if (earliestCreatedAt != null) {
            return earliestCreatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else {
            return System.currentTimeMillis();
        }
    }

    /**
     * Pobiera wszystkie unikalne pary kryptowalut z tabeli PRICE_RECORD.
     */
    public List<CryptoCurrencyPair> getAllCryptoPairs() {
        return cryptoCurrencyPairService.getCryptoPairs();
    }


    /**
     * Usuwa dane cenowe starsze niż określona liczba dni.
     *
     * @param days liczba dni
     */
    @Transactional
    public void cleanOldRecords(int days) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(days);
        int deletedCount = priceRecordRepository.deleteOldRecords(thresholdDate);
        log.info("Deleted {} old data from table PRICE_RECORD", deletedCount);
    }

    /**
     * Agreguje dane cenowe dla danej pary kryptowalut, starsze niż określona liczba dni.
     *
     * @param cryptoPair para kryptowalut
     * @param days liczba dni do agregacji
     * @return lista zagregowanych danych (data, średnia cena)
     */
    public List<Object[]> aggregatePriceData(CryptoCurrencyPair cryptoPair, int days) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(days);
        return priceRecordRepository.aggregatePriceData(cryptoPair, thresholdDate);
    }
}
