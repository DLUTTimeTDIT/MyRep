package factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义ThreadFactory
 */
public class NamedThreadFactory implements ThreadFactory {

    /**
     * 前缀
     */
    private final String prefix;

    /**
     * 是否是守护线程
     */
    private final boolean daemon;

    /**
     * 当前factory的线程数
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory() {
        this("threadPool");
    }

    public NamedThreadFactory(String prefix) {
        this(prefix, true);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {

        this.prefix = prefix;
        this.daemon = daemon;

    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(prefix + threadNumber.getAndIncrement());

        thread.setContextClassLoader(NamedThreadFactory.class.getClassLoader());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(daemon);
        return thread;
    }
}
