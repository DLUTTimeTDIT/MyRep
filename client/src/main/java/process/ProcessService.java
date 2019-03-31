package process;

import common.MetaData;
import exception.QRPCException;
import sun.jvm.hotspot.oops.Metadata;

/**
 * hsf的服务发布和服务消费
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
    void publish(Metadata metadata) throws QRPCException;

}
