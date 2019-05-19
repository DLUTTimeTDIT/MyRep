package utils;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalUtils {

    public static ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(()-> new HashMap<>(4));

    public static <T> T get(final String key){
        Map<String, Object> map = ThreadLocalUtils.threadLocal.get();
        return (T) map.get(key);
    }

    public static void set(final String key, final Object value){
        Map<String, Object> map = ThreadLocalUtils.threadLocal.get();
        map.put(key, value);
    }

    public static void set(final Map<String, Object> map){
        ThreadLocalUtils.threadLocal.get().putAll(map);
    }

    public static void remove(){
        ThreadLocalUtils.threadLocal.remove();
    }

    public static <T> T remove(final String key){
        Map<String, Object> map = ThreadLocalUtils.threadLocal.get();
        return (T) map.remove(key);
    }
}
