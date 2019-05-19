package netty.serialize.impl;

import com.alibaba.fastjson.JSON;
import netty.serialize.Decoder;

public class FastJsonDecoder implements Decoder {
    @Override
    public Object decode(byte[] bytes, Class<?> classType) throws Exception {
        return JSON.parseObject(new String(bytes), classType);
    }

    @Override
    public Object decode(byte[] bytes) throws Exception {
        return JSON.parseObject(new String(bytes));
    }
}
