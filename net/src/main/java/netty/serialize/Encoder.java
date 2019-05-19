package netty.serialize;

/**
 * encoder接口
 */
public interface Encoder {

    String CHAR_SET = "UTF-8";
    /**
     * 将Object encode为 byte数组
     * @param object
     * @return
     * @throws Exception
     */
    byte[] encode(Object object) throws Exception;
}
