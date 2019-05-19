package proxy;

import container.ServiceSingletonContainer;
import exception.QRPCException;
import model.ApplicationModel;
import model.ConsumerMethodModel;
import model.ConsumerServiceModel;
import model.MetaData;
import rpcprotocol.RpcProtocolTemplateService;
import rpcprotocol.impl.RpcProtocolTemplateServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 代理类
 */
public class QRPCServiceProxy implements InvocationHandler {

    private final RpcProtocolTemplateService rpcProtocolTemplateService = ServiceSingletonContainer.getInstance(RpcProtocolTemplateServiceImpl.class);

    private final MetaData metaData;

    private final ConsumerServiceModel consumerServiceModel;

    private final Object instance;

    public QRPCServiceProxy(final MetaData metaData, final Class<?>[] classes) {

        this.metaData = metaData;

        this.instance = Proxy.newProxyInstance(metaData.getInterfaceClazz().getClassLoader(), classes, this);

        consumerServiceModel = new ConsumerServiceModel(metaData, this.instance);

        ApplicationModel.addConsumerService(metaData.getUniqueName(), consumerServiceModel);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ConsumerMethodModel methodModel = consumerServiceModel.getMethodModel(method);
        return this.doInvoke(methodModel, args);
    }

    public Object getInstance() {
        return instance;
    }

    private Object doInvoke(ConsumerMethodModel consumerMethodModel, Object[] args) throws Throwable{
        AtomicInteger availablePoolSize = metaData.getAvailablePoolSize();
        if(availablePoolSize == null){
            return rpcProtocolTemplateService.invokeWithMethodObject(consumerMethodModel, args);
        } else{
            int size = availablePoolSize.decrementAndGet();
            if(size < 0){
                throw new RuntimeException(new QRPCException("server thread pool is full, serviceName:" + metaData.getUniqueName() +
                        " methodName" + consumerMethodModel.getMethodName()));
            } else{
                return rpcProtocolTemplateService.invokeWithMethodObject(consumerMethodModel, args);
            }
        }
    }
}
