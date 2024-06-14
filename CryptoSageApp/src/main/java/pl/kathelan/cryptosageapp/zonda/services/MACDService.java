package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleHistoryResponse;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.String.valueOf;

@Service
@RequiredArgsConstructor
@Slf4j
public class MACDService {

    private final CandleDataService candleDataService;
    private final List<Double> closingPrices = new CopyOnWriteArrayList<>();


    @Scheduled(fixedRate = 300000)
    public void getHistoricalPrices() {
        log.info("Started getting historical prices");
        long startTime = System.currentTimeMillis();
        long pastTime = startTime - 300000;
        CandleHistoryResponse history;
        try {
            if (closingPrices.isEmpty()) {
                history = candleDataService.getHistory(CryptoPair.BTC_PLN.getValue(), startTime - 259200000, startTime, valueOf(259200));
            } else {
                history = candleDataService.getHistory(CryptoPair.BTC_PLN.getValue(), pastTime, startTime, valueOf(300));
            }
            addDataToHistoryList(history);
        } catch (Exception e) {
            log.error("Error getting historical prices", e);
        }
    }

    private void addDataToHistoryList(CandleHistoryResponse candleHistory) {
        candleHistory.getItems().forEach(item -> closingPrices.add(item.getData().getC()));
        log.info("Added {} new closing prices. Total: {}", candleHistory.getItems().size(), closingPrices.size());
        if (closingPrices.size() < 3) {
            log.warn("Not enough data to process MACD. Current size: {}", closingPrices.size());
        } else {
            calculateMACD();
        }
    }

    private void calculateMACD() {
        double[] pricesArray = closingPrices.stream().mapToDouble(Double::doubleValue).toArray();
        double[] macd = calculateMACDValues (pricesArray);
        double[] signalLine = calculateSignalLine(macd);
        Signal latestSignal = generateSignal(macd, signalLine);
        log.info("MACD calculated with latest signal: {}", latestSignal);
    }

    private double[] calculateMACDValues (double[] prices) {
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
