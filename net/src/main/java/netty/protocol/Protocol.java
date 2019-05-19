package netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 自定义网络协议接口
 */
public interface Protocol {

    /**
     * decode
     * @param ctx
     * @param in
     * @param position
     * @return
     */
    Object decode(ChannelHandlerContext ctx, ByteBuf in, int position) throws Exception;
}
