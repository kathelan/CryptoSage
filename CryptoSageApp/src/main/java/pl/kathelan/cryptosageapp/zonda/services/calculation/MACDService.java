package pl.kathelan.cryptosageapp.zonda.services.calculation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.exceptions.DataRetrievalException;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;
import pl.kathelan.cryptosageapp.zonda.services.calculation.trading.WalletOperationService;

import java.util.*;

@Service
@Slf4j
public class MACDService {

    private final List<SignalListener> listeners = new ArrayList<>();
    private final MACDCalculator macdCalculator;
    private final PriceDataService priceDataService;

    public MACDService(MACDCalculator macdCalculator, PriceDataService priceDataService, WalletOperationService walletOperationService) {
        this.macdCalculator = macdCalculator;
        this.priceDataService = priceDataService;
        addSignalListener(walletOperationService);
    }

    @Transactional
    public synchronized void analyzeMarket(CryptoPair cryptoPair) {
        try {
            List<Double> closingPrices = priceDataService.getClosingPrices(cryptoPair);
            if (closingPrices.size() < 26) {
                log.debug("Not enough data to process MACD for {}. Current size: {}", cryptoPair, closingPrices.size());
            } else {
                calculateMACD(cryptoPair, closingPrices);
            }
        } catch (DataRetrievalException e) {
            log.error("Error retrieving data for {}: {}", cryptoPair, e.getMessage());
        }
    }

    public void addSignalListener(SignalListener listener) {
        listeners.add(listener);
    }

    private void notifySignalListeners(CryptoPair cryptoPair, Signal signal) {
        for (SignalListener listener : listeners) {
            listener.onSignalGenerated(cryptoPair, signal);
        }
    }

    private void calculateMACD(CryptoPair cryptoPair, List<Double> closingPrices) {
        double[] macd = macdCalculator.calculate(closingPrices);
        double[] signalLine = macdCalculator.calculateSignalLine(macd);
        Signal latestSignal = generateSignal(macd, signalLine);
        log.debug("MACD calculated for {} with latest signal: {}", cryptoPair, latestSignal);
        notifySignalListeners(cryptoPair, latestSignal);
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
}
