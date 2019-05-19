package netty.utils;

import com.caucho.hessian.io.SerializerFactory;
import netty.serialize.Decoder;
import netty.serialize.Encoder;
import netty.serialize.impl.*;

public class CodecTypeUtils {

    public static final byte JAVA_CODEC = 2;

    public static final byte HESSIAN2_CODEC = 4;

    public static final byte KRYO_CODEC = 5;

    public static final byte JSON_CODEC = 6;

    public static final Encoder ENCODER;

    public static final Decoder DECODER;

    private static Encoder[] encoders = new Encoder[7];

    private static Decoder[] decoders = new Decoder[7];

    private static SerializerFactory serializerFactory;

    static {
        serializerFactory = new SerializerFactory();

        serializerFactory.setAllowNonSerializable(true);

        ENCODER = new Hessian2Encoder();

        addEncoder(HESSIAN2_CODEC, ENCODER);
        addEncoder(JAVA_CODEC, new JavaEncoder());
        addEncoder(KRYO_CODEC, new KyroEncoder());
        addEncoder(JSON_CODEC, new FastJsonEncoder());

        DECODER = new Hessian2Decoder();

        addDecoders(HESSIAN2_CODEC, DECODER);
        addDecoders(JAVA_CODEC, new JavaDecoder());
        addDecoders(KRYO_CODEC, new KyroDecoder());
        addDecoders(JSON_CODEC, new FastJsonDecoder());
    }

    private static void addEncoder(int key, Encoder encoder){
        if(key > encoders.length){
            Encoder[] newEncoders = new Encoder[key + 1];
            System.arraycopy(encoders, 0, newEncoders, 0, encoders.length);
            encoders = newEncoders;
        }
        encoders[key] = encoder;
    }

    private static void addDecoders(int key, Decoder decoder){
        if(key > decoders.length){
            Decoder[] newDecoders = new Decoder[key + 1];
            System.arraycopy(decoders, 0, newDecoders, 0, decoders.length);
            decoders = newDecoders;
        }
        decoders[key] = decoder;
    }

    public static Decoder getDecoder(int key){
        return decoders[key];
    }

    public static Encoder getEncoder(int key){
        return encoders[key];
    }
}
