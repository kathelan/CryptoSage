package pl.kathelan.cryptosageapp.exceptions;

public class PaginationException extends RuntimeException {
    public PaginationException() {
        super("Pagination error occurred");
    }

    public PaginationException(String message) {
        super(message);
    }

    public PaginationException(int page, int totalPages) {
        super("Requested page " + page + " exceeds total pages (" + totalPages + ")");
    }
}
