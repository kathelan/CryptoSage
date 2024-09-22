package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CryptoCalculationService {
    public BigDecimal calculateAmountToBuy(BigDecimal walletAmount, BigDecimal price) {
        return walletAmount.divide(price, 8, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateNewWalletAmount(BigDecimal walletAmount, BigDecimal amountToBuy, BigDecimal price) {
        return walletAmount.subtract(price.multiply(amountToBuy)).setScale(8, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAmountToSell(BigDecimal walletAmount, BigDecimal price, BigDecimal amountToSell) {
        return walletAmount.add(price.multiply(amountToSell)).setScale(8, RoundingMode.HALF_UP);
    }
}
