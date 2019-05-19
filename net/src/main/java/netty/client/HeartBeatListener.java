package netty.client;

import model.QRPCResponse;

/**
 * 心跳listener
 */
public class HeartBeatListener {

    private final Client client;

    public HeartBeatListener(Client client) {
        this.client = client;
    }

    public void onResponse(QRPCResponse qrpcResponse){
        if(qrpcResponse == null || qrpcResponse.isError()){
            if(client.increaseAndGetHbTimes() > 2){
                this.innerCloseConnection();
            }
        } else{
            client.resetHBTimes();
            System.out.println("heartbeat check successfully");
        }
    }

    private void innerCloseConnection(){
        System.out.println("heartbeat failed for three times");
        client.close("heartbeat failed for three times");
    }
}
