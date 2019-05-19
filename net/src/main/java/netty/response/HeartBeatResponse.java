package netty.response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import model.QRPCResponse;
import netty.constants.Constants;
import netty.protocol.impl.HeartBeatProtocol;
import netty.request.BaseRequest;

public class HeartBeatResponse extends BaseResponse {

    public HeartBeatResponse(long requestId) {
        super(Constants.PROTOCOL_FOR_HEARTBEAT, requestId);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {

        out.capacity(HeartBeatProtocol.PROTOCOL_HEAD_LEN);
        out.writeByte(HeartBeatProtocol.HEARTBEAT_PROTOCOL);
        out.writeByte(HeartBeatProtocol.RESPONSE);
        out.writeByte(HeartBeatProtocol.VERSION);
        out.writeByte((byte)0);
        out.writeByte((byte)0);
        out.writeByte((byte)0);
        out.writeLong(this.getRequestId());
    }

    @Override
    public QRPCResponse getResponse(BaseRequest request) {
        throw new IllegalStateException("doesn't not support");
    }
}
