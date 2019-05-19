package rpcprotocol;

import exception.QRPCTimeoutException;
import model.ConsumerMethodModel;
import model.MetaData;
import exception.QRPCException;
import model.QRPCRequest;
import netty.client.RemotingURL;

/**
 * RPC协议接口 负责rpc核心逻辑
 */
public interface RpcProtocolService {

    /**
     * 远程调用
     * @param request
     * @param consumerMethodModel
     * @param remotingURL
     * @return
     */
    Object invoke(QRPCRequest request, ConsumerMethodModel consumerMethodModel, RemotingURL remotingURL) throws QRPCException;

    /**
     * 注册服务提供者
     * @param metaData
     * @throws QRPCException
     */
    void registerProvider(MetaData metaData) throws QRPCException;

    /**
     * 关闭服务并且反注册
     */
    void shutdownGracefully() throws Exception;

}
