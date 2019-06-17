package netty.server.impl;

import factory.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.Timer;
import netty.connection.Connection;
import netty.handler.NettyServerHandler;
import netty.handler.decoder.NettyProtocolDecoder;
import netty.handler.encoder.NettyProtocolEncoder;
import netty.processor.RpcRequestProcessor;
import netty.protocol.ProtocolFactory;
import netty.response.RPCResponse;
import netty.response.ResponseStatus;
import netty.server.Server;
import netty.timedtask.ScanAllClientServerSideTask;
import netty.utils.CodecTypeUtils;
import netty.utils.CommonVar;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyServer implements Server {

    private static final String SERVER_THREAD_POOL_NAME = "nettyServer";

    private static final AtomicBoolean hasStarted = new AtomicBoolean(false);

    private static final Timer timer = CommonVar.timer;

    private final NettyServerHandler serverHandler = new NettyServerHandler();
    /**
     * 接收客户端的tcp链接的EventLoopGroup 线程个数为cpu核*2
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2, new NamedThreadFactory(SERVER_THREAD_POOL_NAME));

    /**
     * 处理IO读写操作，执行系统任务 线程个数为cpu核*2
     */
    private final EventLoopGroup workGroup = CommonVar.WORK_GROUP;

    /**
     * 绑定机器
     */
    private final String bindHost;

    private final RpcRequestProcessor rpcRequestProcessor;

    public NettyServer(RpcRequestProcessor rpcRequestProcessor, String bindHost) {
        ((NioEventLoopGroup)workGroup).setIoRatio(100);
        this.bindHost = bindHost;
        this.rpcRequestProcessor = rpcRequestProcessor;
        ProtocolFactory.getInstance().initServerSide(rpcRequestProcessor);

        timer.newTimeout(new ScanAllClientServerSideTask(serverHandler), 59, TimeUnit.SECONDS);
    }

    public void start(int port) throws Exception {
        if(!hasStarted.compareAndSet(false, true)){
            return;
        }
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // todo: learn about why, why default unpooled?
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new NettyProtocolDecoder())
                                .addLast("encoder", new NettyProtocolEncoder())
                                .addLast("handler", serverHandler);
                    }
                });
        int tryTimes = 3;
        for(int i = 0; i < tryTimes; i++){
            ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(bindHost, port));
            channelFuture.await();
            if(channelFuture.isSuccess()){
                System.out.println("server started, host:" + bindHost + ", port:" + port);
                return;
            }
            else{
                tryTimes--;
                if(tryTimes <= 0){
                    System.out.println("start server failed for 3 times, and it's time to end it");
                    System.exit(1);
                }
                else{
                    System.out.println("failed to start server, host:" + bindHost + " port:" + port + " ,try another time. fail reason" + channelFuture.cause());
                    Thread.sleep(3000);
                }
            }
        }
    }

    public void stop() throws Exception {
        System.out.println("server stop");
        hasStarted.set(false);
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    public void signalClosingServer() {
        RPCResponse rpcResponse = new RPCResponse(-1, CodecTypeUtils.HESSIAN2_CODEC, new byte[1]);
        rpcResponse.setStatus(ResponseStatus.SERVER_CLOSING);
        for(Channel channel : serverHandler.getChannels()){
            channel.writeAndFlush(rpcResponse);
        }
    }

    public void closeConnections() {
        serverHandler.getConnections().forEach(Connection::close);
    }

}
