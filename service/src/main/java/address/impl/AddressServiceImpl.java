package address.impl;

import address.AddressService;
import config.ConfigServer;
import container.ServiceSingletonContainer;
import route.RouteService;
import route.RouteTypeEnum;
import route.impl.ConsistentRouteServiceImpl;
import route.impl.RandomRouteServiceImpl;

import java.util.List;

public class AddressServiceImpl implements AddressService {

    private static final RouteService randomRouteService = ServiceSingletonContainer.getInstance(RandomRouteServiceImpl.class);

    private static final RouteService consistentRouteService = ServiceSingletonContainer.getInstance(ConsistentRouteServiceImpl.class);

    @Override
    public String getServiceAddress(String serviceName, String methodName, String[] paramTypes,
                                    Object[] args, RouteTypeEnum routeTypeEnum) {
        if(RouteTypeEnum.CONSISTENT_HASH == routeTypeEnum){
            return consistentRouteService.selectAddress(getServiceAddresses(serviceName));
        } else{
            return randomRouteService.selectAddress(getServiceAddresses(serviceName));
        }
    }

    @Override
    public List<String> getServiceAddresses(String serviceName) {
        return ConfigServer.getAddress(serviceName);
    }

    @Override
    public void invalidateAddress(String serviceName, String url) {
        ConfigServer.removeAddress(serviceName, url);
    }
}
