package netty.serialize.impl;

import netty.serialize.SerializeConstants;

import static netty.serialize.SerializeConstants.PRE_RUNTIME_EACH_THREAD;
import static netty.serialize.SerializeConstants.TEST_TIME;

public class JavaTest {

    public static void main(String[] args) throws Exception {
        JavaDecoder javaDecoder = new JavaDecoder();
        JavaEncoder javaEncoder = new JavaEncoder();
        long startTime = System.currentTimeMillis();
        long preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
        while(System.currentTimeMillis() < preRunEndTime){
            javaEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
        }

        startTime = System.currentTimeMillis();
        int count = 0;
        while(count < 10000){
            javaEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
            count++;
        }
        System.out.println("Java 序列化0-999组成的数字列表10000次:" + (System.currentTimeMillis() - startTime));

        byte[] bytes = javaEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
        startTime = System.currentTimeMillis();
        preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
        while(System.currentTimeMillis() < preRunEndTime){
            javaDecoder.decode(bytes);
        }

        startTime = System.currentTimeMillis();
        count = 0;
        while(count < 10000){
            javaDecoder.decode(bytes);
            count++;
        }
        System.out.println("Java 反序列化0-999组成的数字列表10000次:" + (System.currentTimeMillis() - startTime));
        System.out.println("Java 反序列化0-999组成的数字列表字节数组大小" + bytes.length);
    }
}
