package netty.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.constants.Constants;
import netty.handler.ServerHandler;
import netty.protocol.ProtocolFactory;
import netty.protocol.impl.RPCProtocol;
import netty.response.BaseResponse;
import netty.response.RPCResponse;
import utils.StringByteTransCacheUtils;
import utils.UUIDGenerator;

/**
 * RPC请求
 */
public class RPCRequest extends BaseRequest {

    private static final byte[] EXTEND_BYTES = new byte[3];

    private final byte codecType;

    private final String targetInstanceName;

    private final String methodName;

    private final String[] argTypes;

    private final byte[][] requestObjects;

    private final byte[] requestProps;

    private final int size;

    private final static ServerHandler<? extends BaseRequest> serverHandler = ProtocolFactory.getInstance()
            .getServerHandler(Constants.PROTOCOL_FOR_QRPC_VERSION);

    public RPCRequest(long requestId, int timeout, String targetInstanceName, String methodName, String[] argTypes, byte[][] requestObjects, byte[] requestProps, byte codecType, int size) {
        super(Constants.PROTOCOL_FOR_QRPC_VERSION, requestId, timeout);
        this.codecType = codecType;
        this.targetInstanceName = targetInstanceName;
        this.methodName = methodName;
        this.argTypes = argTypes;
        this.requestObjects = requestObjects;
        this.requestProps = requestProps;
        this.size = size;

    }

    public String getMethodKey(){
        StringBuilder stringBuilder = new StringBuilder(targetInstanceName);
        stringBuilder.append(methodName);
        for(int i = 0; i < argTypes.length; i++){
            stringBuilder.append(argTypes[i]);
        }
        return stringBuilder.toString();
    }

    public RPCRequest(int timeout, String targetInstanceName, String methodName, String[] argTypes, byte[][] requestObjects, byte[] requestProps, byte codecType, int size) {
        this(UUIDGenerator.getNextId(), timeout, targetInstanceName, methodName, argTypes, requestObjects, requestProps, codecType, size);
    }

    public BaseResponse createErrorResponse(String errorInfo) {
        return new RPCResponse(this.getRequestId(), codecType, StringByteTransCacheUtils.getBytes(errorInfo));
    }

    public ServerHandler<? extends BaseRequest> getServerHandler() {
        return serverHandler;
    }

    public int size() {
        return size;
    }

    public void encode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int requestArgTypesLen = 0;
        int requestArgsLen = 0;
        String[] argTypes = this.getArgTypes();
        byte[][] requestArgTypes = new byte[argTypes.length][];
        for(int i = 0; i < argTypes.length; i++){
            requestArgTypes[i] = StringByteTransCacheUtils.getBytes(argTypes[i]);
        }
        for(byte[] requestArgType : requestArgTypes){
            requestArgTypesLen += requestArgType.length;
        }
        byte[][] requestObjects = this.getRequestObjects();
        if(requestObjects != null){
            for(byte[] requestArg : requestObjects){
                requestArgsLen += requestArg.length;
            }
        }
        byte[] targetInstanceNameBytes = StringByteTransCacheUtils.getBytes(this.getTargetInstanceName());
        byte[] methodNameBytes = StringByteTransCacheUtils.getBytes(this.getMethodName());
        long id = this.getRequestId();
        int timeout = this.getTimeout();
        int requestArgTypesCount = requestArgTypes.length;
        int requestPropLength = this.getRequestProps() == null ? 0 :this.getRequestProps().length;
        int capacity = RPCProtocol.REQUEST_HEADER_LENGTH + requestArgTypesCount * 4 * 2 + 5
                + targetInstanceNameBytes.length + methodNameBytes.length + requestArgTypesLen + requestArgsLen
                + requestPropLength;
        in.capacity(capacity);
        in.writeByte(Constants.PROTOCOL_FOR_QRPC_VERSION);
        in.writeByte(RPCProtocol.VERSION);
        in.writeByte(RPCProtocol.REQUEST);
        in.writeByte(this.getCodecType());
        in.readBytes(EXTEND_BYTES);
        in.writeLong(id);
        in.writeInt(timeout);
        in.writeInt(targetInstanceNameBytes.length);
        in.writeInt(methodNameBytes.length);
        in.writeInt(requestArgTypesCount);
        for(byte[] requestArgType : requestArgTypes){
            in.writeInt(requestArgType.length);
        }
        if(requestObjects != null){
            for(byte[] requestArg : requestObjects){
                in.writeBytes(requestArg);
            }
        }
        if(this.getRequestProps() != null){
            in.writeBytes(this.getRequestProps());
        }
    }

    public byte getCodecType() {
        return codecType;
    }

    public String getTargetInstanceName() {
        return targetInstanceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getArgTypes() {
        return argTypes;
    }

    public byte[][] getRequestObjects() {
        return requestObjects;
    }

    public byte[] getRequestProps() {
        return requestProps;
    }

    public int getSize() {
        return size;
    }
}
