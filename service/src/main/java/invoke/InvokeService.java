package invoke;

import exception.QRPCException;
import model.ConsumerMethodModel;
import model.QRPCRequest;
import netty.client.RemotingURL;

/**
 * 调用服务 处理核心调用逻辑
 */
public interface InvokeService {

    Object invoke(QRPCRequest qrpcRequest, ConsumerMethodModel consumerMethodModel, RemotingURL remotingURL) throws QRPCException;
}
