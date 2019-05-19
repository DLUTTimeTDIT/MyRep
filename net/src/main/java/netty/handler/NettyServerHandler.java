package netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import netty.connection.Connection;
import netty.connection.impl.NettyConnection;
import netty.request.BaseRequest;
import netty.response.BaseResponse;
import netty.response.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<BaseRequest> {

    private final ConcurrentMap<Channel, NettyConnection> channels = new ConcurrentHashMapV8<Channel, NettyConnection>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseRequest msg) throws Exception {
        final Connection connection = channels.get(ctx.channel());
        connection.refreshLastReadTime(System.currentTimeMillis());
        BaseRequest request = msg;
        ServerHandler<BaseRequest> serverHandler = (ServerHandler<BaseRequest>) request.getServerHandler();
        Executor executor = serverHandler.getExecutor(request);
        if(executor == null){
            serverHandler.handleRequest(request, connection, System.currentTimeMillis());
        }
        else{
            try{
                executor.execute(new HandlerRunnable(connection, request, serverHandler));
            }
            catch (Throwable throwable){
                System.out.println("provider thread pool is full." + throwable.getMessage());
                BaseResponse response = request.createErrorResponse("provider thread pool is full.");
                response.setStatus(ResponseStatus.THREADPOOL_BUSY);
                connection.writeResponseToChannel(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.print("something bad happened:");
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.putIfAbsent(channel, new NettyConnection(channel));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
        channels.remove(ctx.channel());
        super.channelInactive(ctx);
    }

    public List<Channel> getChannels(){
        return new ArrayList<Channel>(channels.keySet());
    }

    public List<Connection> getConnections(){
        return new ArrayList<Connection>(channels.values());
    }

    private static class HandlerRunnable implements Runnable {

        private final Connection connection;

        private final BaseRequest message;

        private final ServerHandler<BaseRequest> serverHandler;

        private final long dispatchTime = System.currentTimeMillis();

        public HandlerRunnable(Connection connection, BaseRequest message, ServerHandler<BaseRequest> serverHandler) {
            this.connection = connection;
            this.message = message;
            this.serverHandler = serverHandler;
        }

        public void run() {
            serverHandler.handleRequest(message, connection, dispatchTime);
        }
    }

}
