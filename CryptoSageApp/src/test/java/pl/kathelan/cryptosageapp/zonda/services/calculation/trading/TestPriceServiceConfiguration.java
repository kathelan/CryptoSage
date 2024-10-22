package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;

import java.math.BigDecimal;

@TestConfiguration
public class TestPriceServiceConfiguration {

    @Bean
    @Primary
    public PriceService testPriceService() {
        return new PriceService(null) {
            @Override
            public BigDecimal getPrice(CryptoPair cryptoPair) {
                // Zwróć predefiniowane ceny dla testów
                if ("BTC-USD".equalsIgnoreCase(cryptoPair.getValue())) {
                    return new BigDecimal("50000.00");
                } else if ("ETH-USD".equalsIgnoreCase(cryptoPair.getValue())) {
                    return new BigDecimal("4000.00");
                }
                // Domyślna cena
                return new BigDecimal("1000.00");
            }
        };
    }
}
