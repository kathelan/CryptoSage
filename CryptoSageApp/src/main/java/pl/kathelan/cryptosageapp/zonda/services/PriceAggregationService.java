package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.DailyPriceRecord;
import pl.kathelan.cryptosageapp.zonda.repositories.DailyPriceRecordRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceAggregationService {

    private final PriceRecordService priceRecordService;
    private final DailyPriceRecordRepository dailyPriceRecordRepository;

    /**
     * Agreguje dane godzinowe do dziennych oraz usuwa dane starsze niż 30 dni.
     */
    @Transactional
    public void aggregateAndCleanOldRecords() {
        List<CryptoCurrencyPair> allCryptoPairs = priceRecordService.getAllCryptoPairs();
        for (CryptoCurrencyPair pair : allCryptoPairs) {
            aggregateDailyData(pair);
        }

        priceRecordService.cleanOldRecords(30);
    }

    /**
     * Agreguje dane dla danej pary kryptowalut do dziennych rekordów.
     *
     * @param cryptoPair para kryptowalut
     */
    private void aggregateDailyData(CryptoCurrencyPair cryptoPair) {
        List<Object[]> aggregatedData = priceRecordService.aggregatePriceData(cryptoPair, 30);

        for (Object[] record : aggregatedData) {
            LocalDate date = (LocalDate) record[0];
            Double avgPrice = (Double) record[1];

            DailyPriceRecord dailyRecord = new DailyPriceRecord(avgPrice, cryptoPair, date);
            dailyPriceRecordRepository.save(dailyRecord);

            log.debug("Saved agregated data for {} with date {}: avg price {}", cryptoPair, date, avgPrice);
        }
    }
}

