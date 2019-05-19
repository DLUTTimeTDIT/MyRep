package netty.protocol.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.constants.Constants;
import netty.protocol.Protocol;
import netty.request.HeartBeatRequest;
import netty.response.HeartBeatResponse;

/**
 * 心跳协议
 */
public class HeartBeatProtocol implements Protocol {

    public static final byte HEARTBEAT_PROTOCOL = Constants.PROTOCOL_FOR_HEARTBEAT;

    public static final byte PROTOCOL_HEAD_LEN = 1 * 6 + 3 * 4;

    public static final byte VERSION = (byte)1;

    public static final byte REQUEST = (byte)0;

    public static final byte RESPONSE = (byte)1;

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in, int position) throws Exception {
        if(in.readableBytes() < PROTOCOL_HEAD_LEN - 1){
            in.setIndex(position, in.writerIndex());
            return null;
        }
        byte type = in.readByte();
        byte version = in.readByte();
        if(version == VERSION){
            if(type == REQUEST){
                in.readByte();
                in.readByte();
                in.readByte();
                long requestId = in.readLong();
                int timeout = in.readInt();
                return new HeartBeatRequest(requestId, timeout);
            } else if(type == RESPONSE){
                in.readByte();
                in.readByte();
                in.readByte();
                long requestId = in.readLong();
                in.readInt();
                return new HeartBeatResponse(requestId);
            } else{
                throw new Exception("protocol type " + type + " is not supported");
            }
        } else{
            throw new Exception("protocol version " + version + " is not supported");
        }
    }
}
