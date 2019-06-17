package route;

import route.impl.ConsistentRouteServiceImpl;
import utils.ThreadLocalUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsistentRouteServiceImplTest {

    public static void main(String[] args) {
        List<String> address = new ArrayList<>();
        address.add("123456");
        address.add("123457");
        ThreadLocalUtils.set(ConsistentRouteServiceImpl.CONSISTENT_KEY, "1");
        ConsistentRouteServiceImpl consistentRouteService = new ConsistentRouteServiceImpl();
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 1000;
        int count = 0;
        while(System.currentTimeMillis() < endTime){
            consistentRouteService.selectAddress(address);
            count++;
        }

        System.out.println(count);
    }
}
