package netty.threadpool;

import exception.QRPCException;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池管理器
 */
public interface ThreadPoolManager {

    /**
     * 为服务分配线程池
     * @param serviceName 服务名称
     * @param corePoolSize 核心池大小
     * @param maximumPoolSize 所需最大线程个数
     */
    void allocThreadPool(final String serviceName, int corePoolSize, int maximumPoolSize) throws QRPCException;

    /**
     * 获取指定服务的线程池执行器
     * @param serviceName
     * @return
     */
    Executor getThreadExecutor(String serviceName);

    /**
     * 获取默认的线程池执行器
     * @return
     */
    ThreadPoolExecutor getDefaultPoolExecutor();

    /**
     * 关闭线程池
     */
    void shutdown();
}
