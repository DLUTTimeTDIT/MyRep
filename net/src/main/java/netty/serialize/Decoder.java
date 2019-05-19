package netty.serialize;

/**
 * decoder
 */
public interface Decoder {

    Object decode(byte[] bytes, Class<?> classType) throws Exception;

    Object decode(byte[] bytes) throws Exception;
}
