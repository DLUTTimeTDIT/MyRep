package netty.client;

import model.QRPCRequest;
import netty.request.BaseRequest;
import netty.utils.TransUtils;

import java.util.concurrent.Future;

public abstract class AbstractClient implements Client {

    private final RemotingURL url;

    protected volatile boolean enable = true;

    public AbstractClient(RemotingURL url) {
        this.url = url;
    }

    @Override
    public Future<Object> invoke(QRPCRequest request, byte codecType, int timeout) throws Exception {
        BaseRequest baseRequest = TransUtils.convert(request, codecType, timeout);
        RequestFuture requestFuture = RequestFuture.create(baseRequest);
        sendRequest(baseRequest, requestFuture);
        return requestFuture;
    }

    @Override
    public void close(String cause) {
        doClose(cause);
    }

    /**
     * 让netty去发送请求
     * @param request
     * @param future
     * @throws Exception
     */
    public abstract void sendRequest(BaseRequest request, RequestFuture future) throws Exception;

    /**
     * 让netty去发送请求
     * @param request
     * @param listener
     * @throws Exception
     */
    public abstract void sendRequest(BaseRequest request, HeartBeatListener listener) throws Exception;

    @Override
    public void disableOut() {
        this.enable = false;
    }

    protected abstract void doClose(String cause);

    @Override
    public RemotingURL getUrl(){
        return this.url;
    }
}
