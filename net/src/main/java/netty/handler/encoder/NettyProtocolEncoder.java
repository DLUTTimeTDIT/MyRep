package netty.handler.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import netty.client.RequestFuture;
import netty.constants.Constants;
import netty.protocol.Protocol;
import netty.protocol.ProtocolFactory;
import netty.request.BaseRequest;
import netty.request.HeartBeatRequest;
import netty.request.RPCRequest;
import netty.response.RPCResponse;

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
        if(msg instanceof RequestFuture){
            RequestFuture requestFuture = (RequestFuture)msg;
            BaseRequest baseRequest = requestFuture.getRequest();
            baseRequest.encode(ctx, out);
            return;
        }
        else if(msg instanceof RPCResponse){
            RPCResponse rpcResponse = (RPCResponse)msg;
            rpcResponse.encode(ctx, out);
            return;
        }
        System.out.println("unsupported msg:" + msg);
    }
}
