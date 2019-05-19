package netty.utils;

import factory.NamedThreadFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

// netty中一些公用的实例
public class CommonVar {

    private static final String TIMER_THREAD_POOL_NAME = "nettyTimer";

    public static final Timer timer = new HashedWheelTimer(new NamedThreadFactory(TIMER_THREAD_POOL_NAME));

    public static final EventLoopGroup WORK_GROUP = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2, new NamedThreadFactory());
}
