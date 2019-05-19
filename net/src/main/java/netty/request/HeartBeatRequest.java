package netty.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.constants.Constants;
import netty.handler.ServerHandler;
import netty.protocol.ProtocolFactory;
import netty.protocol.impl.HeartBeatProtocol;
import netty.response.BaseResponse;
import netty.response.HeartBeatResponse;

/**
 * 心跳请求
 */
public class HeartBeatRequest extends BaseRequest {

    private static final ServerHandler<? extends BaseRequest> serverHandler = ProtocolFactory.getInstance().getServerHandler(Constants.PROTOCOL_FOR_HEARTBEAT);

    public HeartBeatRequest(long id, int timeout) {
        super(Constants.PROTOCOL_FOR_HEARTBEAT, id, timeout);
    }

    public HeartBeatRequest() {
        super(Constants.PROTOCOL_FOR_HEARTBEAT);
    }

    @Override
    public BaseResponse createErrorResponse(String errorInfo) {
        return new HeartBeatResponse(this.getRequestId());
    }

    @Override
    public ServerHandler<? extends BaseRequest> getServerHandler() {
        return serverHandler;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        long id = this.getRequestId();
        int timeout = this.getTimeout();
        in.capacity(HeartBeatProtocol.PROTOCOL_HEAD_LEN);
        in.writeByte(HeartBeatProtocol.HEARTBEAT_PROTOCOL);
        in.writeByte(HeartBeatProtocol.REQUEST);
        in.writeByte(HeartBeatProtocol.VERSION);
        in.writeByte((byte)0);
        in.writeByte((byte)0);
        in.writeByte((byte)0);
        in.writeLong(id);
        in.writeInt(timeout);
    }
}
