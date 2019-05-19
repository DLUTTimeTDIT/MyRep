package netty.client;

import io.netty.channel.Channel;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import netty.request.BaseRequest;
import netty.request.HeartBeatRequest;
import netty.utils.CommonVar;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyClient extends AbstractClient {

    private final Channel channel;

    // 心跳检测 27s
    private static final int DEFAULT_HEART_BEAT_TIME = 27;

    private int hbTimes = 0;

    public NettyClient(final RemotingURL url, final Channel channel){
        super(url);
        this.channel = channel;
    }

    @Override
    public void sendRequest(BaseRequest request, RequestFuture future) throws Exception {
        channel.writeAndFlush(future);
    }

    @Override
    public void sendRequest(BaseRequest request, HeartBeatListener listener) throws Exception {
        channel.writeAndFlush(new HeartbeatCallback(request, listener));
    }

    @Override
    protected void doClose(String cause) {
        System.out.println("netty client close:" + cause);
        this.channel.close();
    }

    @Override
    public String getServerIp() {
        return ((InetSocketAddress)(channel.remoteAddress())).getHostName();
    }

    @Override
    public int getServerPort() {
        return ((InetSocketAddress)(channel.remoteAddress())).getPort();
    }

    @Override
    public ClientFactory getClientFactory() {
        return NettyClientFactory.getInstance();
    }

    @Override
    public boolean isEnabled() {
        return channel.isWritable() && enable;
    }

    @Override
    public boolean isConnected() {
        return channel.isActive() && enable;
    }

    @Override
    public void resetHBTimes() {
        hbTimes = 0;
    }

    @Override
    public int increaseAndGetHbTimes() {
        return ++hbTimes;
    }

    @Override
    public void startHeartBeat() throws Exception {
        final BaseRequest hbRequest = new HeartBeatRequest();
        CommonVar.timer.newTimeout(new TimerTask(){
            @Override
            public void run(Timeout timeout) throws Exception {
                try {
                    sendRequest(hbRequest, new HeartBeatListener(NettyClient.this));
                } catch (Throwable throwable){
                    System.out.println("heartbeat error");
                } finally {
                    if(isEnabled()) {
                        CommonVar.timer.newTimeout(this, DEFAULT_HEART_BEAT_TIME, TimeUnit.SECONDS);
                    }
                }
            }
        }, DEFAULT_HEART_BEAT_TIME, TimeUnit.SECONDS);
    }

    @Override
    public void removeAllInvalidRequestCallBack(long now) {
        channel.writeAndFlush(now);
    }
}
