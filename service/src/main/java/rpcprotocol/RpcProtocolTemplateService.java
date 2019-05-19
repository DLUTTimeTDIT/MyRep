package rpcprotocol;

import exception.QRPCException;
import model.ConsumerMethodModel;
import model.MetaData;

/**
 * 负责组装qrpcRequest、地址路由等
 */
public interface RpcProtocolTemplateService {

    /**
     * 基于反射的调用
     *
     * @param consumerMethodModel
     * @param args
     * @return
     * @throws QRPCException
     * @throws Throwable
     */
    Object invokeWithMethodObject(ConsumerMethodModel consumerMethodModel, Object[] args) throws QRPCException, Throwable;

    /**
     * 服务注册提供者
     *
     * @param metaData
     * @throws QRPCException
     */
    void registerProvider(MetaData metaData) throws QRPCException;

    /**
     * 关闭QRPC IO server并反注销
     *
     * @throws QRPCException
     */
    void shutdownQRPCServer() throws QRPCException;

}
