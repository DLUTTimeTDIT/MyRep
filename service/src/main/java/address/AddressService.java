package address;

import route.RouteTypeEnum;

import java.util.List;

/**
 * 地址服务
 */
public interface AddressService {

    /**
     * 获取目标服务的地址
     * @param serviceName
     * @param methodName
     * @param paramTypes
     * @param args
     * @param routeTypeEnum
     * @return
     */
    String getServiceAddress(String serviceName, String methodName, String[] paramTypes, Object[] args, RouteTypeEnum routeTypeEnum);


    /**
     * 配置中心中指定服务的地址
     * @param serviceName
     * @return
     */
    List<String> getServiceAddresses(String serviceName);

    /**
     * 将指定服务的指定url 进行invalidate
     * @param serviceName
     * @param url
     */
    void invalidateAddress(String serviceName, String url);
}
