package route;

import route.impl.RandomRouteServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class RandomRouteServiceImplTest {
    public static void main(String[] args) {
        List<String> address = new ArrayList<>();
        address.add("123456");
        address.add("123457");
        RandomRouteServiceImpl randomRouteService = new RandomRouteServiceImpl();
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 1000;
        int count = 0;
        while(System.currentTimeMillis() < endTime){
            randomRouteService.selectAddress(address);
            count++;
        }

        System.out.println(count);
    }
}
