package netty.client;

import model.QRPCResponse;
import netty.request.BaseRequest;
import netty.response.BaseResponse;

public class HeartbeatCallback implements RequestWrapper {

    private final long startTime = System.currentTimeMillis();

    private final HeartBeatListener listener;

    private final BaseRequest request;

    public HeartbeatCallback(BaseRequest request, HeartBeatListener listener) {

        this.listener = listener;
        this.request = request;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        listener.onResponse(new QRPCResponse());
    }

    @Override
    public void removeAllRequestCallBackWhenClose() {
        QRPCResponse response = new QRPCResponse();
        response.setErrorMsg("connection closed");
        this.getListener().onResponse(response);
    }

    @Override
    public void cleanUpInvalidCallBack() {
        QRPCResponse response = new QRPCResponse();
        response.setErrorMsg("client timeout:" + request.getTimeout());
        this.getListener().onResponse(response);
    }

    public HeartBeatListener getListener() {
        return listener;
    }
    @Override
    public BaseRequest getRequest() {
        return request;
    }
}
