package netty.handler.impl;

import netty.connection.Connection;
import netty.handler.ServerHandler;
import netty.request.HeartBeatRequest;
import netty.response.HeartBeatResponse;

import java.util.concurrent.Executor;

public class HeartBeatServerHandler implements ServerHandler<HeartBeatRequest> {
    @Override
    public void handleRequest(HeartBeatRequest request, Connection connection, long startTime) {
        connection.writeResponseToChannel(new HeartBeatResponse(request.getRequestId()));
    }

    @Override
    public Executor getExecutor(HeartBeatRequest request) {
        return null;
    }
}
