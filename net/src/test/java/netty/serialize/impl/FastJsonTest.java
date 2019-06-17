package netty.serialize.impl;

import netty.serialize.SerializeConstants;

import static netty.serialize.SerializeConstants.PRE_RUNTIME_EACH_THREAD;
import static netty.serialize.SerializeConstants.TEST_TIME;

public class FastJsonTest {

    public static void main(String[] args) throws Exception {
        FastJsonDecoder fastJsonDecoder = new FastJsonDecoder();
        FastJsonEncoder fastJsonEncoder = new FastJsonEncoder();
        long startTime = System.currentTimeMillis();
        long preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
        while(System.currentTimeMillis() < preRunEndTime){
            fastJsonEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
        }

        startTime = System.currentTimeMillis();
        int count = 0;
        while(count < 10000){
            fastJsonEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
            count++;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("FastJSON 序列化0-999组成的数字列表10000次序列化耗时:" + (endTime - startTime));

        byte[] bytes = fastJsonEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
        startTime = System.currentTimeMillis();
        preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
        while(System.currentTimeMillis() < preRunEndTime){
            fastJsonDecoder.decode(bytes);
        }

        startTime = System.currentTimeMillis();
        count = 0;
        while(count < 10000){
            fastJsonDecoder.decode(bytes);
            count++;
        }
        System.out.println("FastJSON 反序列化0-999组成的数字列表反序列化10000次耗时:" + (System.currentTimeMillis() - startTime));
        System.out.println("FastJSON 反序列化0-999组成的数字列表字节数组大小" + bytes.length);
    }
}
