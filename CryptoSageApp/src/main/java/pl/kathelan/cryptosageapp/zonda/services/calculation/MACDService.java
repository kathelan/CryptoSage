package pl.kathelan.cryptosageapp.zonda.services.calculation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.exceptions.DataRetrievalException;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;
import pl.kathelan.cryptosageapp.zonda.services.calculation.trading.WalletOperationService;

import java.util.*;

/**
 * Service responsible for market analysis using the MACD indicator and generating trading signals.
 */
@Service
@Slf4j
public class MACDService {

    private final List<SignalListener> listeners = new ArrayList<>();
    private final MACDCalculator macdCalculator;
    private final PriceDataService priceDataService;

    /**
     * Constructor for MACDService.
     *
     * @param macdCalculator         instance of MACDCalculator.
     * @param priceDataService       service for fetching price data.
     * @param walletOperationService service for handling wallet operations (added as a signal listener).
     */
    public MACDService(MACDCalculator macdCalculator, PriceDataService priceDataService, WalletOperationService walletOperationService) {
        this.macdCalculator = macdCalculator;
        this.priceDataService = priceDataService;
        addSignalListener(walletOperationService);
    }

    /**
     * Analyzes the market for a given cryptocurrency pair, generating trading signals based on the MACD indicator.
     *
     * @param cryptoPair the cryptocurrency pair to analyze.
     */
    @Transactional
    public void analyzeMarket(CryptoPair cryptoPair) {
        List<Double> closingPrices = priceDataService.getClosingPrices(cryptoPair);
        if (closingPrices.size() < 26) {
            log.debug("Not enough data to process MACD for {}. Current size: {}", cryptoPair, closingPrices.size());
        } else {
            calculateMACD(cryptoPair, closingPrices);
        }
    }

    /**
     * Adds a signal listener to receive generated trading signals.
     *
     * @param listener an object implementing the {@link SignalListener} interface.
     */
    public void addSignalListener(SignalListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies registered listeners about a generated signal.
     *
     * @param cryptoPair the cryptocurrency pair for which the signal was generated.
     * @param signal     the generated signal.
     */
    private void notifySignalListeners(CryptoPair cryptoPair, Signal signal) {
        for (SignalListener listener : listeners) {
            listener.onSignalGenerated(cryptoPair, signal);
        }
    }

    /**
     * Calculates the MACD indicator and generates a trading signal for the given cryptocurrency pair.
     *
     * @param cryptoPair    the cryptocurrency pair.
     * @param closingPrices list of closing prices.
     */
    private void calculateMACD(CryptoPair cryptoPair, List<Double> closingPrices) {
        double[] macd = macdCalculator.calculate(closingPrices);
        double[] signalLine = macdCalculator.calculateSignalLine(macd);
        Signal latestSignal = generateSignal(macd, signalLine);
        log.debug("MACD calculated for {} with latest signal: {}", cryptoPair, latestSignal);
        notifySignalListeners(cryptoPair, latestSignal);
    }

    /**
     * Generates a trading signal based on MACD values and the Signal Line.
     *
     * @param macd       array of MACD values.
     * @param signalLine array of Signal Line values.
     * @return the generated trading signal.
     */
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
