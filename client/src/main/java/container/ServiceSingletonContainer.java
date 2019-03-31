package container;

import process.impl.ProcessServiceImpl;

import java.util.HashMap;

/**
 * 服务单例容器
 */
public class ServiceSingletonContainer {

    private static final HashMap<String, Object> SERVICE_CLASS_MAP = new HashMap<String, Object>(){{
        put("process.ProcessService", new ProcessServiceImpl());
    }};

    /**
     * 获取对应类的单例
     * @param clazz
     * @return
     */
    public static Object getInstance(Class<?> clazz){
        if(has(clazz)){
            return SERVICE_CLASS_MAP.get(clazz.getName());
        }
        return null;
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
