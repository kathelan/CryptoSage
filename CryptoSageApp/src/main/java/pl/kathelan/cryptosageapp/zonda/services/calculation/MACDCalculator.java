package pl.kathelan.cryptosageapp.zonda.services.calculation;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class responsible for calculating the MACD (Moving Average Convergence Divergence) indicator.
 * Implements the {@link IndicatorCalculator} interface.
 */
@Service
public class MACDCalculator implements IndicatorCalculator {

    /**
     * Calculates MACD values based on a list of closing prices.
     *
     * @param prices list of closing prices.
     * @return array of MACD values.
     */
    @Override
    public double[] calculate(List<Double> prices) {
        double[] pricesArray = prices.stream().mapToDouble(Double::doubleValue).toArray();
        return calculateMACDValues(pricesArray);
    }

    /**
     * Calculates MACD values based on an array of closing prices.
     *
     * @param prices array of closing prices.
     * @return array of MACD values.
     */
    public double[] calculateMACDValues(double[] prices) {
        double[] shortEMA = calculateEMA(prices, 12);
        double[] longEMA = calculateEMA(prices, 26);
        double[] macd = new double[prices.length];

        for (int i = 0; i < prices.length; i++) {
            macd[i] = shortEMA[i] - longEMA[i];
        }
        return macd;
    }

    /**
     * Calculates the Signal Line based on MACD values.
     *
     * @param macd array of MACD values.
     * @return array of Signal Line values.
     */
    public double[] calculateSignalLine(double[] macd) {
        return calculateEMA(macd, 9);
    }

    /**
     * Calculates the Exponential Moving Average (EMA) for a given period.
     *
     * @param prices array of prices.
     * @param period period for the EMA.
     * @return array of EMA values.
     */
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
