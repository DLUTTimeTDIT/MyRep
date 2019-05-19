package netty.handler.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import netty.protocol.Protocol;
import netty.protocol.ProtocolFactory;

import java.util.List;

public class NettyProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final int readerIndex = in.readerIndex();
        if(in.readableBytes() < 1){
            in.setIndex(readerIndex, in.writerIndex());
            return;
        }
        byte type = in.readByte();
        Protocol protocol = ProtocolFactory.getInstance().getProtocol(type);
        if(protocol == null){
            System.out.println("unsupported protocol type: " + type);
            return;
        }

        Object msg = protocol.decode(ctx, in, readerIndex);
        if(msg != null){
            out.add(msg);
        }
    }

}
