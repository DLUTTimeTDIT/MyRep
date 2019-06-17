package netty.serialize.impl;

import netty.serialize.SerializeConstants;

import static netty.serialize.SerializeConstants.PRE_RUNTIME_EACH_THREAD;
import static netty.serialize.SerializeConstants.TEST_TIME;

public class Hessian2Test {

    public static void main(String[] args) throws Exception {
        Hessian2Decoder hessian2Decoder = new Hessian2Decoder();
        Hessian2Encoder hessian2Encoder = new Hessian2Encoder();
        long startTime = System.currentTimeMillis();
        long preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
        while(System.currentTimeMillis() < preRunEndTime){
            hessian2Encoder.encode(SerializeConstants.SERIALIZE_OBJECT);
        }

        startTime = System.currentTimeMillis();
        int count = 0;
        while(count < 10000){
            hessian2Encoder.encode(SerializeConstants.SERIALIZE_OBJECT);
            count++;
        }
        System.out.println("hessian2 序列化0-999组成的数字列表10000次序列化耗时:" + (System.currentTimeMillis() - startTime));

        byte[] bytes = hessian2Encoder.encode(SerializeConstants.SERIALIZE_OBJECT);
        startTime = System.currentTimeMillis();
        preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
        while(System.currentTimeMillis() < preRunEndTime){
            hessian2Decoder.decode(bytes);
        }

        startTime = System.currentTimeMillis();
        count = 0;
        while(count < 10000){
            hessian2Decoder.decode(bytes);
            count++;
        }
        System.out.println("hessian2 反序列化0-999组成的数字列表10000次:" + (System.currentTimeMillis() - startTime));
        System.out.println("hessian2 反序列化0-999组成的数字列表字节数组大小" + bytes.length);
    }
}
