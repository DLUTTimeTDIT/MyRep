package netty.handler.impl;

import netty.connection.Connection;
import netty.handler.ServerHandler;
import netty.processor.RpcRequestProcessor;
import netty.request.RPCRequest;
import netty.response.RPCResponse;
import netty.response.ResponseStatus;
import netty.serialize.Encoder;
import netty.threadpool.ThreadPoolManager;
import netty.utils.TransUtils;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;

public class RPCServerHandler implements ServerHandler<RPCRequest> {

    private final RpcRequestProcessor rpcRequestProcessor;

    private final ThreadPoolManager threadPoolManager;

    public RPCServerHandler(RpcRequestProcessor rpcRequestProcessor) {
        this.rpcRequestProcessor = rpcRequestProcessor;
        this.threadPoolManager = rpcRequestProcessor.getThreadPoolManager();
    }

    @Override
    public void handleRequest(RPCRequest request, Connection connection, long startTime) {
        try {
            rpcRequestProcessor.handleRequest(TransUtils.convert(request), request, connection, startTime);
        } catch (Exception e) {
            try {
                connection.writeResponseToChannel(new RPCResponse(request.getRequestId(), request.getCodecType(),
                        ResponseStatus.SERVER_ERROR, e.getCause().getMessage().getBytes(Encoder.CHAR_SET)));
            } catch (UnsupportedEncodingException e1) {
                System.out.println("该机器不支持utf-8编码");
            }
        }
    }

    @Override
    public Executor getExecutor(RPCRequest request) {
        return threadPoolManager.getThreadExecutor(request.getTargetInstanceName());
    }
}
