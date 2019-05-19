package provider;

import model.MetaData;
import exception.QRPCException;

/**
 * 服务提供者服务
 */
public interface ProviderService {

    void registerMetadata(MetaData metadata);

    void allocThreadPool(String uniqueName, int corePoolSize, int maxPoolSize) throws QRPCException;

    void startServer() throws QRPCException;

    void signalClosingServer();

    void stopServer() throws Exception;

    void closeConnections();
}
