package netty.connection;

import netty.response.BaseResponse;

import java.net.SocketAddress;

public interface Connection {

    SocketAddress getLocalAddress();

    SocketAddress getRemoteAddress();

    String getPeerIP();

    void refreshLastReadTime(long lastReadTime);

    long getLastReadTime();

    void writeResponseToChannel(BaseResponse response);

    void close();
}
