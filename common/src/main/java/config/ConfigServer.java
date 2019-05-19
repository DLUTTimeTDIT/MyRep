package config;

import java.util.List;
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

    public static final List<String> list = new CopyOnWriteArrayList<String>(){{
        add("127.0.0.1:7001");
        add("127.0.0.1:7002");
        add("127.0.0.1:7003");
    }};
    public static List<String> getAddress(String service){
        return list;
    }

    public static void addAddress(String service, String address){
        list.add(address);
    }

    public static void removeAddress(String service, String address){
        list.remove(address);
    }
}
