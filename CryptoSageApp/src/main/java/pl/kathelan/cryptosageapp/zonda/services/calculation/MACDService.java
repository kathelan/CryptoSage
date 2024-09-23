package pl.kathelan.cryptosageapp.zonda.services.calculation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleHistoryResponse;
import pl.kathelan.cryptosageapp.zonda.mappers.CryptoCurrencyMapper;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;
import pl.kathelan.cryptosageapp.zonda.services.CandleDataService;
import pl.kathelan.cryptosageapp.zonda.services.CryptoCurrencyPairService;
import pl.kathelan.cryptosageapp.zonda.services.calculation.trading.WalletOperationService;

import java.util.*;

import static java.lang.String.valueOf;

@Service
@RequiredArgsConstructor
@Slf4j
public class MACDService {

    private final CandleDataService candleDataService;
    private final CryptoCurrencyPairService cryptoCurrencyPairService;
    @Getter
    private final Map<CryptoPair, Signal> latestSignalMap = new EnumMap<>(CryptoPair.class);
    private final WalletOperationService walletOperationService;
    private final CryptoCurrencyMapper cryptoCurrencyMapper;

    public void getHistoricalPrices(CryptoPair cryptoPair) {
        log.debug("Started getting historical prices for : {}", cryptoPair);
        long startTime = System.currentTimeMillis();
        long pastTime = startTime - 300000;
        CandleHistoryResponse history;

        List<CryptoCurrencyPair> tempList = cryptoCurrencyPairService.getCryptoCurrencyPair(cryptoPair);

        try {
            if (tempList.isEmpty()) {
                history = candleDataService.getHistory(cryptoPair.getValue(), startTime - 259200000, startTime, valueOf(259200));
            } else {
                history = candleDataService.getHistory(cryptoPair.getValue(), pastTime, startTime, valueOf(300));
            }

            addDataToHistoryList(cryptoPair, history);
        } catch (Exception e) {
            log.error("Error getting historical prices for : {}", cryptoPair, e);
        }
    }


    private void addDataToHistoryList(CryptoPair cryptoPair, CandleHistoryResponse candleHistory) {
        List<CryptoCurrencyPair> cryptoCurrencyPairs = cryptoCurrencyPairService.getCryptoCurrencyPair(cryptoPair);
        List<PriceRecord> priceRecords = cryptoCurrencyPairService.getPriceRecords(cryptoCurrencyPairs);
        List<Double> closingPrices = new ArrayList<>(!priceRecords.isEmpty() ? cryptoCurrencyMapper.mapPriceRecordsToDouble(priceRecords)
                : Collections.emptyList());
        List<Double> recordToSave = new ArrayList<>();
        candleHistory.getItems().forEach(item -> {
            var close = item.getData().getC();
            closingPrices.add(close);
            recordToSave.add(close);
        });
        cryptoCurrencyPairService.createCryptoCurrencyPair(cryptoPair, recordToSave);
        log.debug("Added {} new closing prices for {}. Total: {}", candleHistory.getItems().size(), cryptoPair, closingPrices.size());
        if (closingPrices.size() < 26) {
            log.debug("Not enough data to process MACD for {}. Current size: {}", cryptoPair, closingPrices.size());
        } else {
            calculateMACD(cryptoPair, closingPrices);
        }
    }

    private void calculateMACD(CryptoPair cryptoPair, List<Double> closingPrices) {
        double[] pricesArray = closingPrices.stream().mapToDouble(Double::doubleValue).toArray();
        double[] macd = calculateMACDValues(pricesArray);
        double[] signalLine = calculateSignalLine(macd);
        Signal latestSignal = generateSignal(macd, signalLine);
        latestSignalMap.put(cryptoPair, latestSignal);
        log.debug("MACD calculated for {} with latest signal: {}", cryptoPair, latestSignal);
        walletOperationService.performOperations(latestSignalMap);
    }

    private double[] calculateMACDValues(double[] prices) {
        double[] shortEMA = calculateEMA(prices, 12);
        double[] longEMA = calculateEMA(prices, 26);
        double[] macd = new double[prices.length];

        for (int i = 0; i < prices.length; i++) {
            macd[i] = shortEMA[i] - longEMA[i];
        }
        return macd;
    }

    private double[] calculateSignalLine(double[] macd) {
        return calculateEMA(macd, 9);
    }

    private Signal generateSignal(double[] macd, double[] signalLine) {
        int latestIndex = macd.length - 1;
        if (macd[latestIndex] > signalLine[latestIndex] && macd[latestIndex - 1] <= signalLine[latestIndex - 1]) {
            return Signal.BUY;
        } else if (macd[latestIndex] < signalLine[latestIndex] && macd[latestIndex - 1] >= signalLine[latestIndex - 1]) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }

    private double[] calculateEMA(double[] prices, int period) {
        double[] ema = new double[prices.length];
        double multiplier = 2.0 / (period + 1);

        ema[0] = prices[0];

        for (int i = 1; i < prices.length; i++) {
            ema[i] = ((prices[i] - ema[i - 1]) * multiplier) + ema[i - 1];
        }
        return ema;
    }
}
