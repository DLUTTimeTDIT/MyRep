package netty.serialize.impl;

import netty.serialize.Decoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class JavaDecoder implements Decoder {
    @Override
    public Object decode(byte[] bytes, Class<?> classType) throws Exception {
        return this.decode(bytes);
    }

    @Override
    public Object decode(byte[] bytes) throws Exception {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        objectInputStream.readObject();
        objectInputStream.close();
        return objectInputStream;
    }
}
