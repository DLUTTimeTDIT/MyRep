package netty.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.SerializerFactory;
import netty.serialize.Decoder;

import java.io.ByteArrayInputStream;

public class Hessian2Decoder implements Decoder {

    private static final SerializerFactory serializerFactory = new SerializerFactory();

    @Override
    public Object decode(byte[] bytes, Class<?> classType) throws Exception {
        HessianInput hessianInput = new HessianInput(new ByteArrayInputStream(bytes));
        hessianInput.setSerializerFactory(serializerFactory);
        return hessianInput.readObject(classType);
    }

    @Override
    public Object decode(byte[] bytes) throws Exception {
        HessianInput hessianInput = new HessianInput(new ByteArrayInputStream(bytes));
        hessianInput.setSerializerFactory(serializerFactory);
        return hessianInput.readObject();
    }
}
