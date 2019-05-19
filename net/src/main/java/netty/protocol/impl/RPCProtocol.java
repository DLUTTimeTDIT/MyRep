package netty.protocol.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.protocol.Protocol;
import netty.request.RPCRequest;
import netty.response.RPCResponse;
import utils.StringByteTransCacheUtils;

/**
 * request:
 * 第一字节：表示这是QRPC协议
 * 第二字节：版本
 * 第三字节：请求
 * 第四字节：序列化方式
 * 5-7：保留字节
 * 8-15：请求ID
 * 16-19：timeout
 * 20-35：服务名、方法名、方法参数值的长度
 * 36-x：服务名，方法名，方法参数值的值
 * x+1 - X+5：附加信息
 *
 *
 *
 *
 * response：
 * 第一字节：表示这是QRPC协议
 * 第二字节：版本
 * 第三字节：响应
 * 第四字节：状态code
 * 第五字节：序列化方式
 * 6-8：保留字节
 * 9-16：请求ID
 * 17-20：返回值的长度大小
 * 21-x：返回值的值
 */
public class RPCProtocol implements Protocol {

    public static final int REQUEST_HEADER_LENGTH = 1 * 6 + 6 * 4;

    public static final int RESPONSE_HEADER_LENGTH = 1 * 8 + 4 * 3;

    public static final byte VERSION = (byte)1;

    public static final byte REQUEST = (byte)0;

    public static final byte RESPONSE = (byte)1;

    public Object decode(ChannelHandlerContext ctx, ByteBuf in, int position) throws Exception {
        if(in.readableBytes() < 2){
            in.setIndex(position, in.writerIndex());
            return null;
        }
        byte version = in.readByte();
        if(version == VERSION){
            byte type = in.readByte();
            if(type == REQUEST){
                return decodeRequest(ctx, in, position);
            }
            else if(type == RESPONSE){
                return decodeRPCResponse(ctx, in, position);
            }
            else{
                System.out.println("unsupported type:" + type);
                return null;
            }
        }
        else{
            System.out.println("unsupported version:" + version);
            return null;
        }
    }

    private Object decodeRequest(ChannelHandlerContext ctx, ByteBuf in, final int position) {
        if(in.readableBytes() < REQUEST_HEADER_LENGTH - 2){
            in.setIndex(position, in.writerIndex());
            return null;
        }

        byte codecType = in.readByte();
        in.setIndex(in.readerIndex() + 3, in.writerIndex());
        long requestId = in.readLong();
        int timeout = in.readInt();
        int targetInstanceLen = in.readInt();
        int methodNameLen = in.readInt();
        int argsCount = in.readInt();
        int argsInfoLen = argsCount * 4 * 2;
        int expectedLenInfoLen = argsInfoLen + targetInstanceLen + methodNameLen + 4;
        int size = expectedLenInfoLen;
        if(in.readableBytes() < expectedLenInfoLen){
            in.setIndex(position, in.writerIndex());
            return null;
        }
        int expectedLen = 0;
        int[] argsTypeLen = new int[argsCount];
        for(int i = 0; i < argsCount; i++){
            argsTypeLen[i] =in.readInt();
            expectedLen += argsTypeLen[i];
        }
        int[] argsLen = new int[argsCount];
        for(int i = 0; i < argsCount; i++){
            argsLen[i] = in.readInt();
            expectedLen += argsLen[i];
        }
        int requestPropLength = in.readInt();
        expectedLen += requestPropLength;
        byte[] targetInstanceByte = new byte[targetInstanceLen];
        in.readBytes(targetInstanceByte);
        byte[] methodNameByte = new byte[methodNameLen];
        in.readBytes(methodNameByte);

        size += expectedLen;
        if(in.readableBytes() < expectedLen){
            in.setIndex(position, in.writerIndex());
            return null;
        }
        byte[][] argsTypes = new byte[argsCount][];
        for(int i = 0; i < argsCount; i++){
            byte[] argTypeByte = new byte[argsTypeLen[i]];
            in.readBytes(argTypeByte);
            argsTypes[i] = argTypeByte;
        }
        byte[][] args = new byte[argsCount][];
        for(int i = 0; i < argsCount; i++){
            byte[] argsByte = new byte[argsLen[i]];
            in.readBytes(argsByte);
            args[i] = argsByte;
        }
        byte[] requestPropBytes = new byte[requestPropLength];
        in.readBytes(requestPropBytes);

        String[] argTypeStrings = new String[argsTypes.length];
        for(int i = 0; i < argsTypes.length; i++){
            argTypeStrings[i] = StringByteTransCacheUtils.getString(argsTypes[i]);
        }

        return new RPCRequest(requestId, timeout, StringByteTransCacheUtils.getString(targetInstanceByte),
                StringByteTransCacheUtils.getString(methodNameByte), argTypeStrings, args, requestPropBytes, codecType, size);
    }

    private Object decodeRPCResponse(ChannelHandlerContext ctx, ByteBuf out, final int position){
        if(out.readableBytes() < RESPONSE_HEADER_LENGTH - 2){
            out.setIndex(position, out.writerIndex());
            return null;
        }
        byte status = out.readByte();
        byte codecType = out.readByte();
        out.setIndex(out.readerIndex() + 3, out.writerIndex());
        long requestId = out.readLong();
        int bodyLen = out.readInt();
        if(out.readableBytes() < bodyLen){
            out.setIndex(position, out.writerIndex());
            return null;
        }
        byte[] bodyBytes = new byte[bodyLen];
        out.readBytes(bodyBytes);
        return new RPCResponse(requestId, codecType, status, bodyBytes);
    }
}
