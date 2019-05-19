package netty.response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import model.QRPCResponse;
import netty.constants.Constants;
import netty.protocol.impl.RPCProtocol;
import netty.request.BaseRequest;
import netty.serialize.Encoder;
import netty.utils.CodecTypeUtils;

import java.io.UnsupportedEncodingException;

/**
 * QRPC响应
 */
public class RPCResponse extends BaseResponse {

    private static final byte[] EXTENDS_BYTES = new byte[3];

    /**
     * 编码器解码器类型
     */
    private final byte codecType;

    /**
     * 响应结果
     */
    private final byte[] response;

    public RPCResponse(long requestId, byte codecType, byte[] response) {
        super(Constants.PROTOCOL_FOR_QRPC_VERSION, requestId);
        this.codecType = codecType;
        this.response = response;
    }

    public RPCResponse(long requestId, byte codecType, ResponseStatus status, byte[] response) {
        super(Constants.PROTOCOL_FOR_QRPC_VERSION, requestId);
        this.codecType = codecType;
        this.response = response;
        this.setStatus(status);
    }

    public RPCResponse(long requestId, byte codecType, byte status, byte[] response) {
        super(Constants.PROTOCOL_FOR_QRPC_VERSION, requestId);
        this.codecType = codecType;
        this.response = response;
        this.setStatus(status);
    }

    public int size() {
        return response == null ? 0 : response.length;
    }

    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
        byte[] body = this.getResponse();
        int capacity = RPCProtocol.RESPONSE_HEADER_LENGTH + body.length;
        out.capacity(capacity);
        out.writeByte(Constants.PROTOCOL_FOR_QRPC_VERSION);
        out.writeByte(RPCProtocol.VERSION);
        out.writeByte(RPCProtocol.RESPONSE);
        out.writeByte(this.getStatus().getStatus());
        out.writeByte(this.getCodecType());
        out.writeBytes(EXTENDS_BYTES);
        out.writeLong(getRequestId());
        out.writeInt(body.length);
        out.writeBytes(body);
    }

    public QRPCResponse getResponse(BaseRequest request) {
        QRPCResponse qrpcResponse = new QRPCResponse();
        if (getStatus() == ResponseStatus.OK) {
            try {
                byte codecType = this.getCodecType();
                Object responseObject = CodecTypeUtils.getDecoder(codecType).decode(this.getResponse());
                qrpcResponse.setResponse(responseObject);
            } catch (Exception e) {
                System.out.print("client deserialize error");
                e.printStackTrace();
            }
            return qrpcResponse;
        }
        try {
            qrpcResponse.setErrorMsg(new String(this.getResponse(), Encoder.CHAR_SET));
        } catch (UnsupportedEncodingException e) {
            System.out.println("byte to string error");
        }
        return qrpcResponse;
    }

    public byte getCodecType() {
        return codecType;
    }

    public byte[] getResponse() {
        return response;
    }
}
