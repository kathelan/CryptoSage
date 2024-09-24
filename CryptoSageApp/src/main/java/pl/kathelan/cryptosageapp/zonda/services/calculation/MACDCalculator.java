package pl.kathelan.cryptosageapp.zonda.services.calculation;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MACDCalculator implements IndicatorCalculator {
    @Override
    public double[] calculate(List<Double> prices) {
        double[] pricesArray = prices.stream().mapToDouble(Double::doubleValue).toArray();
        return calculateMACDValues(pricesArray);
    }

    public double[] calculateMACDValues(double[] prices) {
        double[] shortEMA = calculateEMA(prices, 12);
        double[] longEMA = calculateEMA(prices, 26);
        double[] macd = new double[prices.length];

        for (int i = 0; i < prices.length; i++) {
            macd[i] = shortEMA[i] - longEMA[i];
        }
        return macd;
    }

    public double[] calculateSignalLine(double[] macd) {
        return calculateEMA(macd, 9);
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
