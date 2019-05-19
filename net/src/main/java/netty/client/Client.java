package netty.client;

import model.QRPCRequest;

import java.util.concurrent.Future;

/**
 * RPC client抽象
 */
public interface Client {

    Future<Object> invoke(QRPCRequest request, byte codecType, int timeout) throws Exception;

    String getServerIp();

    int getServerPort();

    ClientFactory getClientFactory();

    void close(final String cause);

    String toString();

    boolean isEnabled();

    boolean isConnected();

    void resetHBTimes();

    int increaseAndGetHbTimes();

    void disableOut();

    void startHeartBeat() throws Exception;

    RemotingURL getUrl();

    void removeAllInvalidRequestCallBack(long now);
}
