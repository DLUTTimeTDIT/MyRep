package constants;

/**
 * 序列化方式
 */
public enum SerializeTypeEnum {
    /**
     * jdk
     */
    JDK((byte)2),

    /**
     * 哈桑
     */
    HESSIAN((byte)4),

    /**
     * KRYO
     */
    KRYO((byte)5),

    /**
     * fastjson
     */
    FASTJSON((byte)6);

    private byte type;

    SerializeTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
