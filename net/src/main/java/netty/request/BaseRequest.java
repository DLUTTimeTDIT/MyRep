package netty.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.handler.ServerHandler;
import netty.response.BaseResponse;
import utils.UUIDGenerator;

/**
 * 网络请求基类
 */
public abstract class BaseRequest {

    private static final int DEFAULT_TIMEOUT = 3000;

    /**
     * 超时
     */
    private final int timeout;

    /**
     * 协议类型
     */
    private final int protocolType;

    /**
     * 请求id
     */
    private final long requestId;

    public BaseRequest(int protocolType) {
        this(protocolType, DEFAULT_TIMEOUT);
    }

    public BaseRequest(int protocolType, int timeout) {
        this(protocolType, UUIDGenerator.getNextId(), timeout);
    }

    public BaseRequest(int protocolType, long requestId, int timeout) {
        this.timeout = timeout;
        this.requestId = requestId;
        this.protocolType = protocolType;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public long getRequestId() {
        return requestId;
    }

    public abstract BaseResponse createErrorResponse(final String errorInfo);

    public abstract ServerHandler<? extends BaseRequest> getServerHandler();

    public abstract int size();

    public abstract void encode(ChannelHandlerContext ctx, ByteBuf in) throws Exception;
}
