package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类加载维护工具
 */
public class ClassUtils {

    // 类名和类的映射
    private static final Map<String, Class<?>> cache = new ConcurrentHashMap<String, Class<?>>();

    // 加载类
    private static Class<?> load(String className) throws ClassNotFoundException {
        return cache.putIfAbsent(className, Class.forName(className));
    }
}
