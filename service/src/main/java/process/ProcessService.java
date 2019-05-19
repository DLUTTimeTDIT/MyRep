package process;

import model.MetaData;
import exception.QRPCException;

/**
 * qrpc的服务发布和服务消费
 */
public interface ProcessService {

    /**
     * 服务消费 返回代理对象
     * @param metadata
     * @return
     * @throws QRPCException
     */
    Object consume(MetaData metadata) throws QRPCException;

    /**
     * 服务发布
     * @param metadata
     */
    void publish(MetaData metadata) throws QRPCException;

    /**
     * 关闭QRPC IO server
     * @throws QRPCException
     */
    void shutdown() throws Exception;

}
