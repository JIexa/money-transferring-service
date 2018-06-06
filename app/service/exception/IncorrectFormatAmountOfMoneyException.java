package service.exception;

public class IncorrectFormatAmountOfMoneyException extends AccountServiceException {
    public IncorrectFormatAmountOfMoneyException(String message) {
        super(message);
    }

    public IncorrectFormatAmountOfMoneyException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectFormatAmountOfMoneyException(Throwable cause) {
        super(cause);
    }
}
