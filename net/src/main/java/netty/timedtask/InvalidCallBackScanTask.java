package netty.timedtask;

import netty.client.Client;

public class InvalidCallBackScanTask {

    public void visit(final long now, final Client client){
        if(client.isConnected()){
            client.removeAllInvalidRequestCallBack(now);
        } else{
            client.getClientFactory().remove(client);
        }
    }
}
