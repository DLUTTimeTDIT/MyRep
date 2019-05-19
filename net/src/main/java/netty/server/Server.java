package netty.server;

/**
 * 网络服务接口
 */
public interface Server {

    /**
     * 开始服务
     * @param port
     */
    void start(int port) throws Exception;

    /**
     * 停止服务
     */
    void stop() throws Exception;

    /**
     * 关闭服务
     */
    void signalClosingServer();

    /**
     * 关闭连接
     */
    void closeConnections();
}
