package utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal的字符串转byte工具
 */
public class StringByteTransCacheUtils {

    private static final String CHARSET = "UTF-8";

    private static final ThreadLocal<Map<String, byte[]>> STRING2BYTES = ThreadLocal.withInitial(() -> new HashMap<>(2048));

    private static final ThreadLocal<Map<ByteArrayWrapper, String>> BYTES2STRING = ThreadLocal.withInitial(() -> new HashMap<>(2048));

    public static final byte[] getBytes(String string){
        Map<String, byte[]> map = STRING2BYTES.get();
        byte[] result = map.get(string);
        if(result != null){
            return result;
        }
        try {
            result = string.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            System.out.println("该机器不支持" + CHARSET +"编码");
            e.printStackTrace();
            return null;
        }
        if(map.size() < 10192){
            map.put(string, result);
        }
        return result;
    }

    public static String getString(byte[] bytes){
        Map<ByteArrayWrapper, String> map = BYTES2STRING.get();
        String result = map.get(new ByteArrayWrapper(bytes));
        if(result != null){
            return result;
        }
        try {
            result = new String(bytes, CHARSET);
        } catch (UnsupportedEncodingException e) {
            System.out.println("该机器不支持" + CHARSET +"编码");
            e.printStackTrace();
            return null;
        }
        if(map.size() < 10192){
            map.put(new ByteArrayWrapper(bytes), result);
        }
        return result;
    }

    private static final class ByteArrayWrapper{
        private final byte[] bytes;

        public ByteArrayWrapper(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ByteArrayWrapper that = (ByteArrayWrapper) o;
            return Arrays.equals(bytes, that.bytes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }
    }
}
