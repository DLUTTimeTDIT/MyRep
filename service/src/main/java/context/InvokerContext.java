package context;

import netty.client.RemotingURL;

/**
 * 调用上下文
 */
public class InvokerContext {

    private RemotingURL remotingURL;

    private long timeout;

    private Long userId;

    public RemotingURL getRemotingURL() {
        return remotingURL;
    }

    public void setRemotingURL(RemotingURL remotingURL) {
        this.remotingURL = remotingURL;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
