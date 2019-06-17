package container;

import address.impl.AddressServiceImpl;
import invoke.impl.InvokeServiceImpl;
import metadata.service.impl.MetaDataServiceImpl;
import netty.processor.impl.RpcRequestProcessorImpl;
import process.impl.ProcessServiceImpl;
import provider.impl.ProviderServiceImpl;
import route.impl.ConsistentRouteServiceImpl;
import route.impl.RandomRouteServiceImpl;
import rpcprotocol.impl.RpcProtocolServiceImpl;
import rpcprotocol.impl.RpcProtocolTemplateServiceImpl;

import java.util.HashMap;

/**
 * 服务单例容器
 */
public class ServiceSingletonContainer<T>  {

    private static final HashMap<String, Object> SERVICE_CLASS_MAP = new HashMap<>();

    static{
        SERVICE_CLASS_MAP.put(ConsistentRouteServiceImpl.class.getName(), new ConsistentRouteServiceImpl());
        SERVICE_CLASS_MAP.put(InvokeServiceImpl.class.getName(), new InvokeServiceImpl());
        SERVICE_CLASS_MAP.put(RpcRequestProcessorImpl.class.getName(), new RpcRequestProcessorImpl());
        SERVICE_CLASS_MAP.put(MetaDataServiceImpl.class.getName(), new MetaDataServiceImpl());
        SERVICE_CLASS_MAP.put(ProviderServiceImpl.class.getName(), new ProviderServiceImpl());
        SERVICE_CLASS_MAP.put(RpcProtocolServiceImpl.class.getName(), new RpcProtocolServiceImpl());
        SERVICE_CLASS_MAP.put(ProcessServiceImpl.class.getName(), new ProcessServiceImpl());
        SERVICE_CLASS_MAP.put(RandomRouteServiceImpl.class.getName(), new RandomRouteServiceImpl());
        SERVICE_CLASS_MAP.put(AddressServiceImpl.class.getName(), new AddressServiceImpl());
        SERVICE_CLASS_MAP.put(RpcProtocolTemplateServiceImpl.class.getName(), new RpcProtocolTemplateServiceImpl());
    };

    /**
     * 获取对应类的单例
     * @param clazz
     * @return
     */
    public static  <T>  T getInstance(Class<T> clazz){
        if(has(clazz)){
            return (T)SERVICE_CLASS_MAP.get(clazz.getName());
        }
        throw new IllegalArgumentException("unknown class:" + clazz);
    }

    /**
     * 判断是否支持该类
     * @param clazz
     * @return
     */
    public static boolean has(Class<?> clazz){
        return SERVICE_CLASS_MAP.containsKey(clazz.getName());
    }
}
