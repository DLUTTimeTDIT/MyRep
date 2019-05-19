package process.impl;

import generic.GenericService;
import model.ApplicationModel;
import model.ConsumerServiceModel;
import model.MetaData;
import container.ServiceSingletonContainer;
import exception.QRPCException;
import metadata.service.MetaDataService;
import metadata.service.impl.MetaDataServiceImpl;
import process.ProcessService;
import proxy.QRPCServiceProxy;
import rpcprotocol.RpcProtocolService;
import rpcprotocol.impl.RpcProtocolServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class ProcessServiceImpl implements ProcessService {

    // rpc协议服务
    RpcProtocolService rpcProtocolService = ServiceSingletonContainer.getInstance(RpcProtocolServiceImpl.class);

    // 元数据服务
    MetaDataService metaDataService = ServiceSingletonContainer.getInstance(MetaDataServiceImpl.class);

    public Object consume(MetaData metadata) throws QRPCException {
        ConsumerServiceModel consumerServiceModel = ApplicationModel.getConsumerServiceModel(metadata.getUniqueName());
        if(consumerServiceModel != null){
            return consumerServiceModel.getProxy();
        }
        List<Class<?>> interfaces = new ArrayList<>(3);
        if(metadata.getInterfaceClazz() != null){
            interfaces.add(metadata.getInterfaceClazz());
        }
        if(!GenericService.class.equals(metadata.getInterfaceClazz())){
            interfaces.add(GenericService.class);
        }
        Class<?>[] interfacesArr = new Class<?>[interfaces.size()];
        interfaces.toArray(interfacesArr);

        QRPCServiceProxy serviceProxy = new QRPCServiceProxy(metadata, interfacesArr);

        Object proxy = serviceProxy.getInstance();

        metaDataService.subscribe(metadata);
        return proxy;
    }

    public void publish(MetaData metaData) throws QRPCException {

        // todo another thing

        rpcProtocolService.registerProvider(metaData);

        metaDataService.publish(metaData);
    }

    @Override
    public void shutdown() throws Exception {
        rpcProtocolService.shutdownGracefully();
    }
}
