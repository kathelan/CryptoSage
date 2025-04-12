package pl.kathelan.cryptosageapp.exceptions;

import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;

import java.time.LocalDate;

public class PriceDataNotFoundException extends RuntimeException {
    public PriceDataNotFoundException() {
        super("No price data found for the given criteria");
    }

    public PriceDataNotFoundException(String message) {
        super(message);
    }

    public PriceDataNotFoundException(CryptoPair pair, LocalDate startDate, LocalDate endDate) {
        super("No price data found for pair " + pair +
                (startDate != null ? " from " + startDate : "") +
                (endDate != null ? " to " + endDate : ""));
    }
}
