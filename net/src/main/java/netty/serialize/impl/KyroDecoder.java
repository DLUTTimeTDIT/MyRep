package netty.serialize.impl;

import com.esotericsoftware.kryo.io.Input;
import netty.serialize.Decoder;
import netty.utils.KryoUtils;

public class KyroDecoder implements Decoder {
    @Override
    public Object decode(byte[] bytes, Class<?> classType) throws Exception {
        Input input = new Input(bytes);
        return KryoUtils.getKryo().readObject(input, classType);
    }

    @Override
    public Object decode(byte[] bytes) throws Exception {
        Input input = new Input(bytes);
        return KryoUtils.getKryo().readClassAndObject(input);
    }
}
