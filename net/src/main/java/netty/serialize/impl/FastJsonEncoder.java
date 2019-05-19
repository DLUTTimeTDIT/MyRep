package netty.serialize.impl;

import com.alibaba.fastjson.JSON;
import netty.serialize.Encoder;

public class FastJsonEncoder implements Encoder {
    @Override
    public byte[] encode(Object object) throws Exception {
        String text = JSON.toJSONString(object);
        return text.getBytes(CHAR_SET);
    }
}
