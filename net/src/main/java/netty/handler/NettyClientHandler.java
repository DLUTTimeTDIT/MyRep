package netty.handler;

import com.google.common.collect.Maps;
import io.netty.channel.*;
import netty.client.Client;
import netty.client.NettyClientFactory;
import netty.client.RequestFuture;
import netty.client.RequestWrapper;
import netty.response.BaseResponse;
import netty.response.ResponseStatus;

import java.util.Map;

public class NettyClientHandler extends ChannelDuplexHandler {

    private static final String ERROR_MSG = "send request error";

    private final NettyClientFactory factory;

    private final Map<Long, RequestWrapper> mappers = Maps.newHashMap();

    public NettyClientHandler(NettyClientFactory factory) {
        this.factory = factory;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if(msg instanceof BaseResponse){
            final BaseResponse response = (BaseResponse) msg;

            final RequestWrapper requestWrapper = mappers.remove(response.getRequestId());
            if(requestWrapper != null){
                requestWrapper.onResponse(response);
            } else if(response.getRequestId() == -1){
                if(response.getStatus() == ResponseStatus.SERVER_CLOSING) {
                    Client client = factory.getClientByChannel(channelHandlerContext.channel());
                }
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof RequestFuture) {
            RequestFuture requestFuture = (RequestFuture)msg;
            ChannelFuture channelFuture = ctx.writeAndFlush(requestFuture, promise);
            channelFuture.addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()){
                    mappers.put(requestFuture.getRequest().getRequestId(), requestFuture);
                } else{
                    BaseResponse baseResponse = requestFuture.getRequest().createErrorResponse(ERROR_MSG);
                    baseResponse.setStatus(ResponseStatus.COMM_ERROR);
                    requestFuture.set(baseResponse);

                    if(!ctx.channel().isActive()){
                        factory.remove(ctx.channel());
                    }
                }
            });
        }
    }
}
