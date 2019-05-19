package route.impl;

import route.RouteService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机选址大法
 */
public class RandomRouteServiceImpl implements RouteService {
    @Override
    public String selectAddress(List<String> address) {
        if(address == null || address.size() == 0){
            return null;
        }
        int size = address.size();
        if(size == 1){
            return address.get(0);
        }
        int index = ThreadLocalRandom.current().nextInt(size);
        return address.get(index);
    }
}
