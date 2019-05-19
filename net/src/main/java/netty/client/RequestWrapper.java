package netty.client;

import netty.request.BaseRequest;
import netty.response.BaseResponse;

public interface RequestWrapper {

    BaseRequest getRequest();

    long getStartTime();

    void onResponse(BaseResponse baseResponse);

    void removeAllRequestCallBackWhenClose();

    void cleanUpInvalidCallBack();
}
