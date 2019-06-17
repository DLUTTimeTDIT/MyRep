package netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.handler.NettyClientHandler;
import netty.handler.decoder.NettyProtocolDecoder;
import netty.handler.encoder.NettyProtocolEncoder;
import netty.timedtask.InvalidCallBackScanTask;
import netty.timedtask.ScanAllClientClientSideTask;
import netty.utils.CommonVar;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class NettyClientFactory extends AbstractClientFactory {

    private static NettyClientFactory INSTANCE = new NettyClientFactory();

    private final Map<Channel, Client> channelClientMap = new ConcurrentHashMap<>();

    private static final int DEFAULT_CONNECT_TIMEOUT = 4000;

    public NettyClientFactory() {
        CommonVar.timer.newTimeout(new ScanAllClientClientSideTask(this, new InvalidCallBackScanTask()), 60, TimeUnit.SECONDS);
    }

    public static NettyClientFactory getInstance(){
        return INSTANCE;
    }

    public List<Channel> getAllChannels(){
        return new ArrayList<>(channelClientMap.keySet());
    }

    @Override
    protected Client createClient(RemotingURL remotingURL) throws Exception {
        final Bootstrap bootstrap = new Bootstrap();
        NettyClientHandler handler = new NettyClientHandler(this);
        bootstrap.group(CommonVar.WORK_GROUP)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("encoder", new NettyProtocolEncoder())
                                .addLast("decoder", new NettyProtocolDecoder())
                                .addLast("handler", handler);
                    }
                });
        String targetIp = remotingURL.getHost();
        int targetPort = remotingURL.getPort();
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(targetIp, targetPort));
        if(channelFuture.awaitUninterruptibly(DEFAULT_CONNECT_TIMEOUT) && channelFuture.isSuccess() && channelFuture.channel().isActive()) {
            Channel channel = channelFuture.channel();
            Client client = new NettyClient(remotingURL, channel);
            channelClientMap.put(channel, client);
            return client;
        } else{
            channelFuture.cancel(true);
            channelFuture.channel().close();
            System.out.println("failed to connect to " + targetIp);
            throw new Exception("targetIp:" + targetIp + ", targetPort" + targetPort + " timeout" + DEFAULT_CONNECT_TIMEOUT);
        }
    }

    public void remove(final Channel channel){
        Client client = channelClientMap.remove(channel);
        if(channel != null){
            super.remove(client);
        }
    }

    public Client getClientByChannel(final Channel channel){
        return channelClientMap.get(channel);
    }

}
