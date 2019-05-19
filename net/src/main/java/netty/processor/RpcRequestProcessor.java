package netty.processor;

import model.QRPCRequest;
import netty.connection.Connection;
import netty.request.RPCRequest;
import netty.threadpool.ThreadPoolManager;

import java.util.concurrent.Executor;

/**
 * RPC处理器接口
 */
public interface RpcRequestProcessor {

    /**
     * 业务线程处理逻辑
     * @param qrpcRequest
     * @param rpcRequest
     * @param connection
     */
    void handleRequest(QRPCRequest qrpcRequest, RPCRequest rpcRequest, Connection connection, long startTime);

    /**
     * 根据请求获取业务线程池
     * @param serviceName
     * @return
     */
    Executor getExecutor(final String serviceName);

    /**
     * 获取线程池管理器
     * @return
     */
    ThreadPoolManager getThreadPoolManager();
}
