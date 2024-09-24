package pl.kathelan.cryptosageapp.zonda.services.calculation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.exceptions.DataRetrievalException;
import pl.kathelan.cryptosageapp.utils.TimeIntervalUtil;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleHistoryResponse;
import pl.kathelan.cryptosageapp.zonda.mappers.CryptoCurrencyMapper;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;
import pl.kathelan.cryptosageapp.zonda.services.CandleDataService;
import pl.kathelan.cryptosageapp.zonda.services.CryptoCurrencyPairService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceDataService {
    private final CandleDataService candleDataService;
    private final CryptoCurrencyPairService cryptoCurrencyPairService;
    private final CryptoCurrencyMapper cryptoCurrencyMapper;


    public List<Double> getClosingPrices(CryptoPair cryptoPair) throws DataRetrievalException {
        log.debug("Starting fetching data for closing prices for: {}", cryptoPair);

        List<CryptoCurrencyPair> existingData = cryptoCurrencyPairService.getCryptoCurrencyPairWithRecords(cryptoPair);

        try {
            boolean isExistingDataPresent = !existingData.isEmpty();
            TimeIntervalUtil.TimeInterval timeInterval = TimeIntervalUtil.determineTimeInterval(isExistingDataPresent);
            CandleHistoryResponse history = fetchCandleHistory(cryptoPair, timeInterval);
            List<Double> closingPrices = extractClosingPrices(existingData);
            List<Double> newClosingPrices = extractNewClosingPrices(history);
            closingPrices.addAll(newClosingPrices);
            saveNewClosingPrices(cryptoPair, newClosingPrices);
            log.debug("Added {} new closing prices for {}. Total: {}", newClosingPrices.size(), cryptoPair, closingPrices.size());
            return closingPrices;

        } catch (Exception e) {
            log.error("Error while trying to fetch closing prices for: {}", cryptoPair, e);
            throw new DataRetrievalException("Unable to fetch closing prices for pair:  " + cryptoPair, e);
        }

    }

    private CandleHistoryResponse fetchCandleHistory(CryptoPair cryptoPair, TimeIntervalUtil.TimeInterval timeInterval) {
        return candleDataService.getHistory(
                cryptoPair.getValue(),
                timeInterval.startTime(),
                timeInterval.endTime(),
                timeInterval.interval()
        );
    }

    private List<Double> extractClosingPrices(List<CryptoCurrencyPair> cryptoCurrencyPairs) {
        List<PriceRecord> priceRecords = cryptoCurrencyPairService.getExistingPriceRecords(cryptoCurrencyPairs);
        List<Double> closingPrices = cryptoCurrencyMapper.mapPriceRecordsToDouble(priceRecords);
        return new ArrayList<>(closingPrices);
    }

    private List<Double> extractNewClosingPrices(CandleHistoryResponse candleHistory) {
        return candleHistory.getItems().stream()
                .map(item -> item.getData().getC())
                .collect(Collectors.toList());
    }

    private void saveNewClosingPrices(CryptoPair cryptoPair, List<Double> newClosingPrices) {
        cryptoCurrencyPairService.createCryptoCurrencyPair(cryptoPair, newClosingPrices);
    }
}
