package netty.serialize.impl;

import netty.serialize.SerializeConstants;

import static netty.serialize.SerializeConstants.PRE_RUNTIME_EACH_THREAD;
import static netty.serialize.SerializeConstants.TEST_TIME;

public class KryoTest {

    public static void main(String[] args) throws Exception {
        KryoDecoder kryoDecoder = new KryoDecoder();
        KryoEncoder kryoEncoder = new KryoEncoder();
        long startTime = System.currentTimeMillis();
        long preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
        while(System.currentTimeMillis() < preRunEndTime){
            kryoEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
        }

        startTime = System.currentTimeMillis();
        int count = 0;
        while(count < 10000){
            kryoEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
            count++;
        }
        System.out.println("Kryo 序列化0-999组成的数字列表10000化次:" + (System.currentTimeMillis() - startTime));

        byte[] bytes = kryoEncoder.encode(SerializeConstants.SERIALIZE_OBJECT);
        startTime = System.currentTimeMillis();
        preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
        while(System.currentTimeMillis() < preRunEndTime){
            kryoDecoder.decode(bytes);
        }

        startTime = System.currentTimeMillis();
        count = 0;
        while(count < 10000){
            kryoDecoder.decode(bytes);
            count++;
        }
        System.out.println("Kryo 反序列化0-999组成的数字列表10000次:" + (System.currentTimeMillis() - startTime));
        System.out.println("Kryo 反序列化0-999组成的数字列表字节数组大小" + bytes.length);
    }
}
