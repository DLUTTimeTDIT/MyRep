package rpcprotocol.impl;

import container.ServiceSingletonContainer;
import exception.QRPCException;
import invoke.InvokeService;
import invoke.impl.InvokeServiceImpl;
import metadata.service.MetaDataService;
import metadata.service.impl.MetaDataServiceImpl;
import model.*;
import netty.client.RemotingURL;
import provider.ProviderService;
import provider.impl.ProviderServiceImpl;
import rpcprotocol.RpcProtocolService;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RPC协议服务 负责远程调用核心逻辑
 */
public class RpcProtocolServiceImpl implements RpcProtocolService {

    private final AtomicBoolean isProviderStarted = new AtomicBoolean(false);

    private final MetaDataService metaDataService = ServiceSingletonContainer.getInstance(MetaDataServiceImpl.class);

    private final ProviderService providerService = ServiceSingletonContainer.getInstance(ProviderServiceImpl.class);

    private final InvokeService invokeService = ServiceSingletonContainer.getInstance(InvokeServiceImpl.class);

    @Override
    public Object invoke(QRPCRequest request, ConsumerMethodModel consumerMethodModel, RemotingURL remotingURL) throws QRPCException {
        return invokeService.invoke(request, consumerMethodModel, remotingURL);
    }

    public void registerProvider(final MetaData metaData) throws QRPCException {

        if (isProviderStarted.compareAndSet(false, true)) {
            providerService.startServer();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        shutdownGracefully();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // 分配线程池
        int corePoolSize = metaData.getCorePoolSize();
        int maxPoolSize = metaData.getMaxPoolSize();
        if (corePoolSize > 0 && maxPoolSize > 0 && maxPoolSize >= corePoolSize) {
            providerService.allocThreadPool(metaData.getUniqueName(), corePoolSize, maxPoolSize);
        }

        // 注册对象到QRPC Server上
        providerService.registerMetadata(metaData);
    }

    @Override
    public void shutdownGracefully() throws Exception {
        List<ProviderServiceModel> providerServiceModels = ApplicationModel.allProviderServices();
        providerServiceModels.forEach(providerServiceModel -> {
            metaDataService.unregister(providerServiceModel.getMetaData());
        });

        Thread.sleep(10000);
        providerService.stopServer();
    }
}
