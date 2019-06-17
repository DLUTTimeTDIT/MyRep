package config;

import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import diamond.ServiceAddressDiamondUtil;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 配置中心 先这样做 写死在本地 后面上diamond
 */
public class ConfigServer {

    /**
     * 服务端口号
     */
    public static final int SERVER_PORT = 12200;

    public static final int MIN_POOL_SIZE = 50;

    public static final int MAX_POOL_SIZE = 600;

    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    private static final List<String> LIST = new CopyOnWriteArrayList<>();

    static {
        LIST.add("172.20.10.4");
    }

    public static List<String> getAddress(String service){
        return LIST;
    }

    public static void addAddress(String service, String address){
        LIST.add(address);
    }

    public static void removeAddress(String service, String address){
        LIST.remove(address);
    }
}
