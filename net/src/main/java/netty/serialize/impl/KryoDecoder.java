package netty.serialize.impl;

import com.esotericsoftware.kryo.io.Input;
import netty.serialize.Decoder;
import netty.utils.KryoUtils;

public class KryoDecoder implements Decoder {
    @Override
    public Object decode(byte[] bytes, Class<?> classType) throws Exception {
        Input input = new Input(bytes);
        Object object = KryoUtils.getKryo().readObject(input, classType);
        input.close();
        return object;
    }

    @Override
    public Object decode(byte[] bytes) throws Exception {
        Input input = new Input(bytes);
        Object object = KryoUtils.getKryo().readClassAndObject(input);
        input.close();
        return object;
    }
}
