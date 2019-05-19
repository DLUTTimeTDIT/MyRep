package exception;

/**
 * QPRC系统异常
 */
public class QRPCException extends Exception {

    /**
     * 异常信息
     */
    private String errMsg;

    public QRPCException(String errMsg){
        super(errMsg);
    }

    public QRPCException(String errMsg, Throwable throwable){
        super(errMsg, throwable);
    }

}
