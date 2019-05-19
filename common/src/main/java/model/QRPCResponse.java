package model;

import java.io.Serializable;

/**
 * QPRC响应类
 */
public class QRPCResponse implements Serializable {

    /**
     * 框架是否出现错误
     */
    private boolean isError = false;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 返回结果
     */
    private Object response;

    /**
     * 服务端处理错误信息
     */
    private String errorType;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        isError = true;
        this.errorMsg = errorMsg;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return "QRPCResponse{" +
                "isError=" + isError +
                ", errorMsg='" + errorMsg + '\'' +
                ", response=" + response +
                ", errorType='" + errorType + '\'' +
                '}';
    }
}
