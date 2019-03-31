package exception;

/**
 * 参数校验异常
 */
public class ValidateException extends Exception {

    private String errMsg;
    public ValidateException(String errMsg){
        super(errMsg);
    }
}
