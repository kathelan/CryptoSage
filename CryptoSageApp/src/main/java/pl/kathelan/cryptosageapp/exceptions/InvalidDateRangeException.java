package pl.kathelan.cryptosageapp.exceptions;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException() {
        super("Invalid date range provided");
    }

    public InvalidDateRangeException(String message) {
        super(message);
    }

    public InvalidDateRangeException(LocalDate startDate, LocalDate endDate) {
        super("End date (" + endDate + ") cannot be before start date (" + startDate + ")");
    }
}
