package netty.response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import model.QRPCResponse;
import netty.request.BaseRequest;

/**
 * response基类
 */
public abstract class BaseResponse {

    private final int protocolType;

    private final long requestId;

    private ResponseStatus status = ResponseStatus.OK;

    public BaseResponse(int protocolType, long requestId) {
        this.protocolType = protocolType;
        this.requestId = requestId;
    }

    public abstract int size();

    public abstract void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception;

    public ResponseStatus getStatus(){
        return status;
    }

    public void setStatus(ResponseStatus status){
        this.status = status;
    }

    public void setStatus(byte status){
        this.status = ResponseStatus.fromCode(status);
    }

    public int getProtocolType() {
        return protocolType;
    }

    public long getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return protocolType + ":" + status;
    }

    public abstract QRPCResponse getResponse(BaseRequest request);
}
