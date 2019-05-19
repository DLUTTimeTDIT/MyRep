package container;

import address.impl.AddressServiceImpl;
import invoke.impl.InvokeServiceImpl;
import netty.processor.impl.RpcRequestProcessorImpl;
import process.impl.ProcessServiceImpl;
import provider.impl.ProviderServiceImpl;
import route.impl.ConsistentRouteServiceImpl;
import route.impl.RandomRouteServiceImpl;
import rpcprotocol.impl.RpcProtocolServiceImpl;

import java.util.HashMap;

/**
 * 服务单例容器
 */
public class ServiceSingletonContainer<T>  {

    private static final HashMap<String, Object> SERVICE_CLASS_MAP = new HashMap<String, Object>(){{
        put("process.impl.ProcessServiceImpl", new ProcessServiceImpl());
        put("rpcprotocol.impl.RpcProtocolServiceImpl", new RpcProtocolServiceImpl());
        put("provider.impl.ProviderServiceImpl", new ProviderServiceImpl());
        put("netty.processor.impl.RpcRequestProcessorImpl", new RpcRequestProcessorImpl());
        put("route.impl.RandomRouteServiceImpl", new RandomRouteServiceImpl());
        put("route.impl.ConsistentRouteServiceImpl", new ConsistentRouteServiceImpl());
        put("address.impl.AddressServiceImpl", new AddressServiceImpl());
        put("invoke.impl.InvokeServiceImpl", new InvokeServiceImpl());
    }};

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
