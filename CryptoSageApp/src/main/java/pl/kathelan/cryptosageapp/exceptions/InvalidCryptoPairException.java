package pl.kathelan.cryptosageapp.exceptions;

public class InvalidCryptoPairException extends RuntimeException {
    public InvalidCryptoPairException() {
        super("Invalid cryptocurrency pair provided");
    }

    public InvalidCryptoPairException(String pairName) {
        super("Invalid cryptocurrency pair: " + pairName);
    }

    public InvalidCryptoPairException(String message, Throwable cause) {
        super(message, cause);
    }
}
