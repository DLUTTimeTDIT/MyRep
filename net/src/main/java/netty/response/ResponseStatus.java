package netty.response;

/**
 * 响应状态枚举类
 */
public enum ResponseStatus {

    /**
     * 成功
     */
    OK((byte)20, "OK"),

    /**
     * 客户端超时
     */
    CLIENT_TIMEOUT((byte)30, "client timeout"),

    /**
     * 服务器超时
     */
    SERVER_TIMEOUT((byte)31, "server timeout"),

    /**
     * 请求无效
     */
    BAD_REQUEST((byte)40, "bad request"),

    /**
     * 响应无效
     */
    BAD_RESPONSE((byte)50, "bad response"),

    /**
     * 服务无法找到
     */
    SERVICE_NOT_FOUND((byte)60, "service not found"),

    /**
     * 服务端错误
     */
    SERVER_ERROR((byte)70, "service error"),

    /**
     * 客户端错误
     */
    CLIENT_ERROR((byte)80, "client error"),

    /**
     * 未知错误
     */
    UNKNOWN_ERROR((byte)91, "unknown error"),

    /**
     * 线程池满
     */
    THREADPOOL_BUSY((byte)81, "thread pool is full"),

    /**
     * 通信错误
     */
    COMM_ERROR((byte)82, "communication error"),

    /**
     * 服务器即将关闭
     */
    SERVER_CLOSING((byte)88, "server will close soon"),
    /**
     * 服务器发送编码
     */
    SERVER_GETCODER((byte)10, "server send coders"),
    /**
     * 未知code
     */
    UNKNOW_CODE((byte)83, "unknown code");

    private final String message;

    private final byte status;

    ResponseStatus(byte status, String message) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public byte getStatus() {
        return status;
    }

    public static ResponseStatus fromCode(byte code){
        ResponseStatus[] statuses = ResponseStatus.values();
        for(int i = 0; i < statuses.length; i++){
            if(statuses[i].status == code){
                return statuses[i];
            }
        }
        return null;
    }
}
