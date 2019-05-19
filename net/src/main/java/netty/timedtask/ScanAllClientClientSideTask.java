package netty.timedtask;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import netty.client.Client;
import netty.client.NettyClientFactory;
import netty.utils.CommonVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScanAllClientClientSideTask implements TimerTask {

    private final List<InvalidCallBackScanTask> taskList = new ArrayList<>();

    private final NettyClientFactory nettyClientFactory;

    public ScanAllClientClientSideTask(NettyClientFactory nettyClientFactory, final InvalidCallBackScanTask... tasks) {
        this.nettyClientFactory = nettyClientFactory;
        if(tasks != null){
            this.taskList.addAll(Arrays.asList(tasks));
        }
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        final long now = System.currentTimeMillis();
        try{
            List<Client> clients = nettyClientFactory.retrieveAllClient();
            if(clients != null){
                clients.forEach(client -> {
                    this.taskList.forEach(task->{
                        task.visit(now, client);
                    });
                });
            }
        } catch (Throwable throwable){
            System.out.println("error occurs where client scan all task");
        } finally {
            CommonVar.timer.newTimeout(this, 60, TimeUnit.SECONDS);
        }
    }
}
