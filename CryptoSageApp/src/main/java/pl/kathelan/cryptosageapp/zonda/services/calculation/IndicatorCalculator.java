package pl.kathelan.cryptosageapp.zonda.services.calculation;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IndicatorCalculator {
    double[] calculate(List<Double> prices);
}
