package netty.serialize.impl;

import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import netty.serialize.Encoder;

import java.io.ByteArrayOutputStream;

public class Hessian2Encoder implements Encoder {

    private static final SerializerFactory serializerFactory = new SerializerFactory();
    @Override
    public byte[] encode(Object object) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        hessianOutput.setSerializerFactory(serializerFactory);
        hessianOutput.writeObject(object);
        return byteArrayOutputStream.toByteArray();
    }

    private static SerializerFactory getSerializerFactory() {
        return serializerFactory;
    }
}
