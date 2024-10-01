package pl.kathelan.cryptosageapp.exceptions;

public class DataRetrievalException extends RuntimeException {
    public DataRetrievalException() {
        super();
    }

    public DataRetrievalException(String message) {
        super(message);
    }

    public DataRetrievalException(String message, Exception error) {
        super(message, error);
    }
}