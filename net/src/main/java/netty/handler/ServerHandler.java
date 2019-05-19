package netty.handler;

import netty.connection.Connection;
import netty.request.BaseRequest;

import java.util.concurrent.Executor;

public interface ServerHandler<T extends BaseRequest> {

    void handleRequest(T request, Connection connection, long startTime);

    Executor getExecutor(final T request);
}
