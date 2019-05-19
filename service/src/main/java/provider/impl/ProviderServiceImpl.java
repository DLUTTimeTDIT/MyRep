package provider.impl;

import config.ConfigServer;
import container.ServiceSingletonContainer;
import model.ApplicationModel;
import model.MetaData;
import exception.QRPCException;
import model.ProviderServiceModel;
import netty.processor.RpcRequestProcessor;
import netty.processor.impl.RpcRequestProcessorImpl;
import netty.server.Server;
import netty.server.impl.NettyServer;
import netty.threadpool.ThreadPoolManager;
import netty.utils.NetUtils;
import provider.ProviderService;

/**
 * 维护服务端线程池 兼听RPC端口
 */
public class ProviderServiceImpl implements ProviderService {

    private final RpcRequestProcessor processor = ServiceSingletonContainer.getInstance(RpcRequestProcessorImpl.class);

    private final Server server = new NettyServer(processor, NetUtils.getIP());

    private final ThreadPoolManager threadPoolManager = processor.getThreadPoolManager();

    /**
     * 服务提供端口号
     */
    private static final int SERVER_PORT = ConfigServer.SERVER_PORT;

    public void registerMetadata(MetaData metadata) {
        ApplicationModel.addProviderService(metadata.getUniqueName(), new ProviderServiceModel(metadata.getUniqueName(), metadata, metadata.getTarget()));
    }

    public void allocThreadPool(String uniqueName, int corePoolSize, int maxPoolSize) throws QRPCException {
        threadPoolManager.allocThreadPool(uniqueName, corePoolSize, maxPoolSize);
    }

    public synchronized void startServer() throws QRPCException {
        try {
            server.start(SERVER_PORT);
        } catch (Exception e) {
            throw new QRPCException("qrpc start failed", e);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("qprc  server started:port" + SERVER_PORT + " I/O thread counts:" + Runtime.getRuntime().availableProcessors() + 1)
                .append(" minPoolSize:").append(ConfigServer.MIN_POOL_SIZE).append(" maxPoolSize:").append(ConfigServer.MAX_POOL_SIZE);
    }

    public void signalClosingServer() {
        server.signalClosingServer();
    }

    public void stopServer() throws Exception {
        server.stop();
    }

    public void closeConnections() {
        server.closeConnections();
    }
}
