package invoke.impl;

import exception.QRPCException;
import exception.QRPCTimeoutException;
import invoke.InvokeService;
import model.ConsumerMethodModel;
import model.QRPCRequest;
import model.QRPCResponse;
import netty.client.Client;
import netty.client.ClientFactory;
import netty.client.NettyClientFactory;
import netty.client.RemotingURL;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InvokeServiceImpl implements InvokeService {

    private final ClientFactory clientFactory = NettyClientFactory.getInstance();

    @Override
    public Object invoke(QRPCRequest qrpcRequest, ConsumerMethodModel consumerMethodModel, RemotingURL remotingURL) throws QRPCException {
        int timeout = 3000;
        if(consumerMethodModel.getTimeout() > 0){
            timeout = consumerMethodModel.getTimeout();
        }
        QRPCResponse qrpcResponse = null;
        try{
            Client client = clientFactory.get(remotingURL);
            // todo 为了调试 把时延设长一点
//            Future<Object> future = client.invoke(qrpcRequest, remotingURL.getSerializeType(), consumerMethodModel.getMetaData().getExecuteTimeout());
            Future<Object> future = client.invoke(qrpcRequest, remotingURL.getSerializeType(), Integer.MAX_VALUE);
            qrpcResponse = (QRPCResponse) (future.get(timeout, TimeUnit.MILLISECONDS));
        } catch (TimeoutException e) {
            throw new RuntimeException(new QRPCTimeoutException(e.getMessage() + " timeout:" + timeout, e));
        } catch (Exception e) {
            throw new RuntimeException(new QRPCException(e.getMessage(), e));
        }

        if(qrpcResponse.isError()){
            if(qrpcResponse.getResponse() instanceof Throwable){
                throw new RuntimeException(qrpcResponse.getErrorMsg(), (Throwable) qrpcResponse.getResponse());
            } else{
                throw new RuntimeException(qrpcResponse.getErrorMsg());
            }
        }
        return qrpcResponse.getResponse();
    }
}
