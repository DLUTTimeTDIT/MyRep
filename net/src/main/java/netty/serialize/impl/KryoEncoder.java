package netty.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import netty.serialize.Encoder;
import netty.utils.KryoUtils;

public class KryoEncoder implements Encoder {

    public static final int BUFFER_SIZE = 2048;

    public static final int MAX_BUFFER_SIZE = 10485760;


    @Override
    public byte[] encode(Object object) throws Exception {
        Kryo kryo = KryoUtils.getKryo();

        Output output = new Output(BUFFER_SIZE, MAX_BUFFER_SIZE);
        kryo.writeClassAndObject(output, object);

        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
}
