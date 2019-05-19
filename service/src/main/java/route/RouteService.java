package route;

import java.util.List;

public interface RouteService {

    /**
     * 选址策略接口
     * @param address
     * @return
     */
    String selectAddress(List<String> address);
}
