package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类加载维护工具
 */
public class ClassUtils {

    // 类名和类的映射
    private static final Map<String, Class<?>> cache = new ConcurrentHashMap<String, Class<?>>();


    static {
        cache.put("void", void.class);
        cache.put("boolean", boolean.class);
        cache.put("byte", byte.class);
        cache.put("char", char.class);
        cache.put("double", double.class);
        cache.put("float", float.class);
        cache.put("int", int.class);
        cache.put("long", long.class);
        cache.put("short", short.class);
    }

    public static Class<?> name2class(String className) throws ClassNotFoundException {
        return cache.putIfAbsent(className, Class.forName(className));
    }
}
