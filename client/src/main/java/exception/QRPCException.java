package exception;

/**
 * QPRC服务发布消费异常
 */
public class QRPCException extends Exception {

    /**
     * 异常信息
     */
    private String errMsg;

    public QRPCException(String errMsg){
        this.errMsg = errMsg;
    }

}
