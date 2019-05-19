package netty.connection.impl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import netty.connection.Connection;
import netty.response.BaseResponse;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Netty链接类
 */
public class NettyConnection implements Connection {

    private Channel channel;

    private String peerIP;

    private volatile long lastReadTime = System.currentTimeMillis();

    public NettyConnection() {
        this.channel = null;
        this.peerIP = null;
    }

    public NettyConnection(Channel channel) {
        this.channel = channel;
        this.peerIP = ((InetSocketAddress)channel.remoteAddress()).getAddress().getHostAddress();
    }

    public SocketAddress getLocalAddress() {
        return channel.localAddress();
    }

    public SocketAddress getRemoteAddress() {
        return channel.remoteAddress();
    }

    public String getPeerIP() {
        return peerIP;
    }

    public void refreshLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void writeResponseToChannel(final BaseResponse response) {
        if(response != null){
            ChannelFuture channelFuture = channel.writeAndFlush(response);
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()){
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("response error, ").append("peerIP:").append(getPeerIP()).append("cause").append(future.cause());
                        System.out.println(stringBuilder.toString());
                        if(!channel.isActive()){
                            channel.close();
                        }
                    }
                }
            });
        }
    }

    public void close() {
        this.channel.close();
    }
}
