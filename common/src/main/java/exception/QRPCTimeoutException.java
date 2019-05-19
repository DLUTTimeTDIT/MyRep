package exception;

public class QRPCTimeoutException extends QRPCException {

    public QRPCTimeoutException(String errorMsg) {
        super(errorMsg);
    }

    public QRPCTimeoutException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }
}
