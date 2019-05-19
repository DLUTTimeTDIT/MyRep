package netty.threadpool.impl;

import exception.QRPCException;
import factory.NamedThreadFactory;
import netty.threadpool.ThreadPoolManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolManagerImpl implements ThreadPoolManager {

    private static final String BIZ_THREAD_POOL_NAME = "biz-processor";

    private static final long keepAliveTime = 300L;

    private final ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();

    private final ThreadPoolExecutor defaultPoolExecutor;

    private final Map<String, ThreadPoolExecutor> poolCache = new HashMap<String, ThreadPoolExecutor>();

    public ThreadPoolManagerImpl(int corePoolSize, int maximumPoolSize) {
        // 使用SynchronousQueue作为阻塞队列 防止请求堆积
        final BlockingQueue<Runnable> workQueue = new SynchronousQueue<Runnable>();
        final ThreadFactory threadFactory = new NamedThreadFactory(BIZ_THREAD_POOL_NAME);
        defaultPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                workQueue, threadFactory, abortPolicy);
    }

    public void allocThreadPool(String serviceName, int corePoolSize, int maximumPoolSize) throws QRPCException {
        // 为同一个服务开辟两次线程池 直接抛出异常
        if(poolCache.containsKey(serviceName)) {
            throw new QRPCException(serviceName + "had existed in the thread pool!");
        }

        // 线程池已关闭
        if(defaultPoolExecutor == null || defaultPoolExecutor.isShutdown()){
            throw new QRPCException("thread pool had closed, serviceName" + serviceName + " alloc failed");
        }

        // 最大线程个数 实际上是剩余线程个数
        int balance = defaultPoolExecutor.getMaximumPoolSize();

        // 剩余线程数量小于所申请的线程数量
        if(balance < maximumPoolSize){
            throw new QRPCException("thread pool need " + maximumPoolSize + ", but " + balance + " left");
        }

        ThreadFactory threadFactory = new NamedThreadFactory(BIZ_THREAD_POOL_NAME + "-" + serviceName);
        try{
            // 不同服务分配不同线程池
            ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                    TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory, abortPolicy);
            poolCache.put(serviceName, executor);
        }catch (Exception e){
            throw new QRPCException("Thread pool allocate failed");
        }

        int newBalance = balance - maximumPoolSize;

        // 还有一个默认的线程池
        if(newBalance == 0){
            defaultPoolExecutor.shutdown();
        } else{
            if(newBalance < defaultPoolExecutor.getCorePoolSize()){
                defaultPoolExecutor.setCorePoolSize(newBalance);
            }
            defaultPoolExecutor.setMaximumPoolSize(newBalance);
        }
    }

    public Executor getThreadExecutor(String serviceName) {
        if(!poolCache.isEmpty()){
            ThreadPoolExecutor executor = poolCache.get(serviceName);
            if(executor != null){
                return executor;
            }
        }
        return defaultPoolExecutor;
    }

    public ThreadPoolExecutor getDefaultPoolExecutor() {
        return defaultPoolExecutor;
    }

    public void shutdown() {
        if(!defaultPoolExecutor.isShutdown()){
            defaultPoolExecutor.shutdown();
        }
        if(!poolCache.isEmpty()){
            poolCache.values().forEach(ThreadPoolExecutor::shutdown);
        }
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        poolCache.keySet().forEach(serviceName -> {
            ThreadPoolExecutor threadPoolExecutor = poolCache.get(serviceName);
            stringBuilder.append("服务:").append(serviceName).append(" 核心线程个数:")
                    .append(threadPoolExecutor.getCorePoolSize()).append(" 最大线程个数:")
                    .append(threadPoolExecutor.getMaximumPoolSize()).append(" 活跃线程个数:")
                    .append(threadPoolExecutor.getActiveCount()).append("\n");
        });

        if(!defaultPoolExecutor.isShutdown()){
            stringBuilder.append("默认使用的线程池的核心线程数量: " + defaultPoolExecutor.getCorePoolSize())
                    .append(" 最大线程数量:" + defaultPoolExecutor.getMaximumPoolSize() + " 活跃线程数量:" + defaultPoolExecutor.getActiveCount());
        }
        return stringBuilder.toString();
    }
}
