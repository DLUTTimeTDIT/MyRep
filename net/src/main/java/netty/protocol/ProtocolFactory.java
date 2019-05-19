package netty.protocol;

import netty.constants.Constants;
import netty.handler.ServerHandler;
import netty.handler.impl.HeartBeatServerHandler;
import netty.handler.impl.RPCServerHandler;
import netty.processor.RpcRequestProcessor;
import netty.protocol.impl.HeartBeatProtocol;
import netty.protocol.impl.RPCProtocol;

/**
 * 协议工厂类
 */
public final class ProtocolFactory {

    private final Protocol[] protocolHandlers = new Protocol[256];
    private final ServerHandler<?>[] serverHandlers = new ServerHandler[256];

    private static ProtocolFactory instance = new ProtocolFactory();

    public static ProtocolFactory getInstance(){
        return instance;
    }

    private ProtocolFactory(){
        registerProtocol(Constants.PROTOCOL_FOR_QRPC_VERSION, new RPCProtocol());
        registerProtocol(Constants.PROTOCOL_FOR_HEARTBEAT, new HeartBeatProtocol());
    }

    public void initServerSide(final RpcRequestProcessor rpcRequestProcessor){
        registerServerHandler(Constants.PROTOCOL_FOR_QRPC_VERSION, new RPCServerHandler(rpcRequestProcessor));
        registerServerHandler(Constants.PROTOCOL_FOR_HEARTBEAT, new HeartBeatServerHandler());
    }

    public Protocol getProtocol(byte type){
        return protocolHandlers[type & 0x0FF];
    }

    public ServerHandler<?> getServerHandler(byte type){
        return serverHandlers[type & 0x0FF];
    }

    private void registerProtocol(byte type, Protocol protocol){
        if(protocolHandlers[type & 0x0FF] != null){
            throw new RuntimeException("protocol header's sign is overlapped");
        }
        protocolHandlers[type & 0x0FF] = protocol;
    }

    private void registerServerHandler(byte type ,final ServerHandler<?> serverHandler){
        serverHandlers[type & 0x0FF] = serverHandler;
    }

}
