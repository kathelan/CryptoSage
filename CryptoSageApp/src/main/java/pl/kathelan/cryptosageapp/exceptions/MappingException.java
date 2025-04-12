package pl.kathelan.cryptosageapp.exceptions;

public class MappingException extends RuntimeException {
    public MappingException() {
        super("Error occurred during entity mapping");
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(Class<?> sourceClass, Class<?> targetClass) {
        super("Failed to map from " + sourceClass.getSimpleName() +
                " to " + targetClass.getSimpleName());
    }
}
