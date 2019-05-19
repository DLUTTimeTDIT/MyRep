package netty.handler.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * encoder pipeline
 */

@ChannelHandler.Sharable
public class NettyProtocolEncoder extends MessageToByteEncoder<Object> {

    public NettyProtocolEncoder() {

        super(false);

    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

    }
}
