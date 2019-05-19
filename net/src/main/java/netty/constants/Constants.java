package netty.constants;

/**
 * 常量类
 */
public interface Constants {

    /**
     * QRPC报文第一个byte内容
     */
    byte PROTOCOL_FOR_QRPC_VERSION = (byte) 0x0D;

    /**
     * 心跳报文的第一个byte的内容
     */
    byte PROTOCOL_FOR_HEARTBEAT = (byte) 0x0C;

    /**
     * 握手报文第一个byte内容
     */
    byte PROTOCOL_FOR_HANDSHAKE = (byte)0x0F;

}
