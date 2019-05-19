package netty.serialize.impl;

import netty.serialize.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class JavaEncoder implements Encoder {
    @Override
    public byte[] encode(Object object) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(object);
        outputStream.flush();
        outputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
