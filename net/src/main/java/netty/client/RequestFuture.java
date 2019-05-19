package netty.client;

import com.google.common.util.concurrent.AbstractFuture;
import model.QRPCResponse;
import netty.request.BaseRequest;
import netty.response.BaseResponse;
import netty.response.ResponseStatus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class RequestFuture extends AbstractFuture<Object> implements RequestWrapper {

    private final BaseRequest request;

    private final long startTime = System.currentTimeMillis();

    public static RequestFuture create(BaseRequest request){
        return new RequestFuture(request);
    }

    private RequestFuture(BaseRequest request){
        this.request = request;
    }

    @Override
    public QRPCResponse get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        BaseResponse response = (BaseResponse) super.get(request.getTimeout(), TimeUnit.MILLISECONDS);
        return response.getResponse(request);
    }

    @Override
    public boolean set(Object value) {
        return super.set(value);
    }

    public BaseRequest getRequest(){
        return request;
    }

    public long getStartTime(){
        return startTime;
    }

    public void onResponse(BaseResponse response){
        this.set(response);
    }

    @Override
    public void removeAllRequestCallBackWhenClose(){
        BaseResponse response = this.request.createErrorResponse("connection close");
        response.setStatus(ResponseStatus.COMM_ERROR);
        this.set(response);
    }

    public void cleanUpInvalidCallBack(){
        BaseResponse baseResponse = this.request.createErrorResponse("time out:" + request.getTimeout());
        baseResponse.setStatus(ResponseStatus.CLIENT_TIMEOUT);
        this.set(baseResponse);
    }
}
