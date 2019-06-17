package netty.protocol;

import netty.constants.Constants;
import netty.protocol.impl.RPCProtocol;
import netty.request.RPCRequest;
import netty.response.RPCResponse;
import utils.StringByteTransCacheUtils;


public class EncodeDecodeRPCTest {

    private static RPCRequest constructRequest(){

        return MockRequest.getInstance();
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 1000;
        int count = 0;
        while(System.currentTimeMillis() < endTime){
            encode();
            count++;
        }
        System.out.println(count);
    }
//
//    public static RPCRequest decode(byte[] bytes){
//
//    }

    public static byte[] encode() {
        RPCRequest rpcRequest = constructRequest();
        int requestArgTypesLen = 0;
        int requestArgsLen = 0;
        String[] argTypes = rpcRequest.getArgTypes();
        byte[][] requestArgTypes = new byte[argTypes.length][];
        for(int i = 0; i < argTypes.length; i++){
            requestArgTypes[i] = StringByteTransCacheUtils.getBytes(argTypes[i]);
        }
        for(byte[] requestArgType : requestArgTypes){
            requestArgTypesLen += requestArgType.length;
        }
        byte[][] requestObjects = rpcRequest.getRequestObjects();
        if(requestObjects != null){
            for(byte[] requestArg : requestObjects){
                requestArgsLen += requestArg.length;
            }
        }
        byte[] targetInstanceNameBytes = StringByteTransCacheUtils.getBytes(rpcRequest.getTargetInstanceName());
        byte[] methodNameBytes = StringByteTransCacheUtils.getBytes(rpcRequest.getMethodName());
        long id = rpcRequest.getRequestId();
        int timeout = rpcRequest.getTimeout();
        int requestArgTypesCount = requestArgTypes.length;
        int requestPropLength = rpcRequest.getRequestProps() == null ? 0 :rpcRequest.getRequestProps().length;
        int capacity = RPCProtocol.REQUEST_HEADER_LENGTH + requestArgTypesCount * 4 * 2 + 5
                + targetInstanceNameBytes.length + methodNameBytes.length + requestArgTypesLen + requestArgsLen
                + requestPropLength;
        byte[] bytes = new byte[capacity];
        int loc = 0;
        bytes[loc++] = Constants.PROTOCOL_FOR_QRPC_VERSION;
        bytes[loc++] = RPCProtocol.VERSION;
        bytes[loc++] = RPCProtocol.REQUEST;
        bytes[loc++] = rpcRequest.getCodecType();
        bytes[loc++] = 0;
        bytes[loc++] = 0;
        bytes[loc++] = 0;
        byte[] idBytes = longToBytes(id);
        appendBytes(bytes, loc, idBytes);
        loc += idBytes.length;
        byte[] timoutBytes = int2Bytes(timeout);
        appendBytes(bytes, loc, timoutBytes);
        loc += timoutBytes.length;
        appendBytes(bytes, loc, targetInstanceNameBytes);
        loc += targetInstanceNameBytes.length;
        appendBytes(bytes, loc, methodNameBytes);
        loc += methodNameBytes.length;
        byte[] requestArgTypesCountBytes = int2Bytes(requestArgTypesCount);
        appendBytes(bytes, loc, requestArgTypesCountBytes);
        loc += requestArgTypesCountBytes.length;
        for(byte[] requestArgType : requestArgTypes){
            appendBytes(bytes, loc, requestArgType);
            loc += requestArgType.length;
        }
        if(requestObjects != null){
            for(byte[] requestArg : requestObjects){
                appendBytes(bytes, loc, requestArg);
                loc += requestArg.length;
            }
        }
        if(rpcRequest.getRequestProps() != null){
            appendBytes(bytes, loc, rpcRequest.getRequestProps());
        }
        return bytes;
    }

    public static byte[] longToBytes(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    public static long bytesToLong(byte[] buffer) {
        long  values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8; values|= (buffer[i] & 0xff);
        }
        return values;
    }

    // byte数组长度为4, bytes[3]为高8位
    public static int bytes2Int(byte[] bytes){
        int value=0;
        value = ((bytes[3] & 0xff)<<24)|
                ((bytes[2] & 0xff)<<16)|
                ((bytes[1] & 0xff)<<8)|
                (bytes[0] & 0xff);
        return value;
    }

    public static byte[] int2Bytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    public static void appendBytes(byte[] bytes, int appendLoc, byte[] appendBytes){
        for(int i = 0; i < appendBytes.length; i++){
            bytes[appendLoc++] = appendBytes[i];
        }
    }
}
