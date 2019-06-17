package netty.timedtask;

import factory.NamedThreadFactory;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import netty.connection.Connection;
import netty.handler.NettyServerHandler;
import netty.utils.CommonVar;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 扫描所有连接的任务线程
 */
public class ScanAllClientServerSideTask implements TimerTask {

    private static final long THRESHOLD_TIME = 59000;

    private final NettyServerHandler serverHandler;

    public ScanAllClientServerSideTask(NettyServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public void run(Timeout timeout) throws Exception {
        try{
            long now = System.currentTimeMillis();
            List<Connection> connections = serverHandler.getConnections();
            if(connections != null){
                for(final Connection connection : connections){
                    if(now - connection.getLastReadTime() > THRESHOLD_TIME){
                        System.out.println("connection " + connection + " will be closed by server");
                        connection.close();
                    }
                }
            }
        }
        catch (Throwable throwable){
            System.out.println("scan client task error");
        } finally {
            CommonVar.timer.newTimeout(this, 59, TimeUnit.SECONDS);
        }
    }
}
