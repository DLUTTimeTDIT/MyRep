package netty.processor.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import model.*;
import netty.connection.Connection;
import netty.processor.RpcRequestProcessor;
import netty.request.RPCRequest;
import netty.response.RPCResponse;
import netty.response.ResponseStatus;
import netty.serialize.Encoder;
import netty.utils.CodecTypeUtils;
import netty.threadpool.ThreadPoolManager;
import netty.threadpool.impl.ThreadPoolManagerImpl;
import utils.ClassUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * RpcRequestProcessor实现类
 */
public class RpcRequestProcessorImpl implements RpcRequestProcessor {

    // 先写死 后面看看需不需要上diamond
    private static final double QPS = 1000;

    private static final String CHAR_SET = "UTF-8";

    private static final String CLIENT_NAME = "client name";

    private static final String UNKOWN = "unknown";

    private static final boolean needLimit = false;

    private static final int QRPC_SERVER_COOR_POOL_SIZE = 50;

    private static final int QRPC_SERVER_MAX_POOL_SIZE = 600;

    private static final String GENERIC_INVOKE = "$invoke";

    private static final ThreadPoolManager threadPoolManager = new ThreadPoolManagerImpl(QRPC_SERVER_COOR_POOL_SIZE, QRPC_SERVER_MAX_POOL_SIZE);

    /**
     * 用来做限流 基于漏斗算法
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(QPS);

    public void handleRequest(QRPCRequest qrpcRequest, RPCRequest rpcRequest, Connection connection, long startTime) {
        final String serviceName = qrpcRequest.getServiceName();
        final String methodName = qrpcRequest.getMethodName();
        final String clientIp = connection.getPeerIP();
        ProviderServiceModel providerServiceModel = ApplicationModel.getProviderServiceModel(serviceName);

        if(providerServiceModel == null) {
            System.out.println("RpcRequestProcessorImpl#handleRequest, 找不到服务" + serviceName);
            return;
        }
        ProviderMethodModel providerMethodModel = providerServiceModel.getMethodModel(methodName, qrpcRequest.getMethodParamTypes());
        if(providerMethodModel == null){
            System.out.println("RpcRequestProcessorImpl#handleRequest, 找不到方法" + methodName);
            return;
        }

        QRPCResponse qrpcResponse = doHandleRequest(qrpcRequest, connection);

        if(qrpcResponse != null){
            writeResponse(rpcRequest, connection, startTime, qrpcResponse);
        }
    }

    public Executor getExecutor(String serviceName) {
        return threadPoolManager.getThreadExecutor(serviceName);
    }

    public ThreadPoolManager getThreadPoolManager() {
        return threadPoolManager;
    }

    public int writeResponse(RPCRequest rpcRequest, Connection connection, final long startTime, QRPCResponse qrpcResponse){
        int responseSize = 0;

        if(qrpcResponse.isError()){
            byte[] responseObject = null;
            try {
                responseObject = qrpcResponse.getErrorMsg().getBytes(CHAR_SET);
            } catch (UnsupportedEncodingException e) {
                System.out.println("该机器不支持utf-8编码");
            }
            responseSize = responseObject.length;
            connection.writeResponseToChannel(new RPCResponse(rpcRequest.getRequestId(), rpcRequest.getCodecType(),
                    ResponseStatus.SERVER_ERROR, responseObject));
            return responseSize;
        }
        try{
            byte codecType = rpcRequest.getCodecType();
            Encoder encoder = CodecTypeUtils.getEncoder(codecType);
            byte[] responseObject = encoder.encode(qrpcResponse.getResponse());
            responseSize = responseObject.length;

            if(System.currentTimeMillis() - startTime >= rpcRequest.getTimeout() ){
                StringBuilder stringBuilder = new StringBuilder("RpcRequestProcessorImpl#writeResponse timeout ");
                stringBuilder.append("requestId:").append(rpcRequest.getRequestId()).append(" serviceName:").append(rpcRequest.getTargetInstanceName())
                        .append(" methodName:").append(rpcRequest.getMethodName()).append(" timeout:").append(rpcRequest.getTimeout())
                        .append(" cost:").append(System.currentTimeMillis() - startTime);
                System.out.println(stringBuilder.toString());
            } else{
                connection.writeResponseToChannel(new RPCResponse(rpcRequest.getRequestId(), rpcRequest.getCodecType(), ResponseStatus.OK, responseObject));
            }

            return responseSize;

        } catch (Exception exception){
            System.out.print("RpcRequestProcessorImpl#writeResponse, Provider error:" + connection);
            exception.printStackTrace();
            byte[] responseObject = null;
            try {
                responseObject = "exception happened on serialization".getBytes(Encoder.CHAR_SET);
                return responseObject.length;
            } catch (UnsupportedEncodingException e) {
                System.out.println("the machine doesn't support utf-8 code");
                return 0;
            }
        }
    }

    private QRPCResponse doHandleRequest(QRPCRequest qrpcRequest, Connection connection){
        final QRPCResponse qrpcResponse = new QRPCResponse();
        if(needLimit){
            if(!tryAcquire()){
                // 被限流
                qrpcResponse.setErrorType("Rate limit");
                qrpcResponse.setErrorMsg("request has been limited");
                return qrpcResponse;
            }
        }
        final String serviceName = qrpcRequest.getServiceName();
        final ProviderServiceModel providerServiceModel = ApplicationModel.getProviderServiceModel(serviceName);
        final String remoteHost = connection.getPeerIP();

        if(null == providerServiceModel){
            System.out.println("RpcRequestProcessorImpl#doHandleRequest,service doesn't exist:" + serviceName);
            qrpcResponse.setErrorMsg("doHandleRequest,service doesn't exist" + serviceName);
            qrpcResponse.setErrorType("service doesn't exist");
            return qrpcResponse;
        }

        String[] argTypes = qrpcRequest.getMethodParamTypes();
        boolean isGeneric = false;

        ProviderMethodModel providerMethodModel = null;

        String methodName = qrpcRequest.getMethodName();
        try{
            if(methodName.equals(GENERIC_INVOKE) && argTypes != null && argTypes.length == 3){
                Object[] methodArgs = qrpcRequest.getMethodArgs();
                methodName = ((String)(methodArgs[0])).trim();
                argTypes = (String[])methodArgs[1];
                for(int i = 0; i < argTypes.length; i++){
                    try{
                        argTypes[i] = ClassUtils.name2class(argTypes[i]).getName();
                    } catch (ClassNotFoundException classNotFoundException){

                    }
                    isGeneric = true;
                }
            }

            providerMethodModel = providerServiceModel.getMethodModel(methodName, argTypes);
            if(providerMethodModel == null){
                System.out.println("method doesn't exist");
                throw new NoSuchMethodException("method doesn't exist, methodName:" + methodName + ", argTypes:" + Arrays.toString(argTypes));
            }

            Method method = providerMethodModel.getMethod();

            Object[] methodArgs = qrpcRequest.getMethodArgs();
            if(isGeneric){
                Class<?>[] params = method.getParameterTypes();
                if(methodArgs == null){
                    methodArgs = new Object[params.length];
                }

                if(methodArgs.length != ((Object[])methodArgs[2]).length || params.length != methodArgs.length){
                    throw new IllegalArgumentException("args.length does not equal types.length");
                }
            }

            Object invokeResult = method.invoke(providerServiceModel.getServiceInstance(), methodArgs);

            if(isGeneric){
                invokeResult = JSON.toJSON(invokeResult);
            }
            qrpcResponse.setResponse(invokeResult);
        } catch (Throwable throwable){
            throwable = throwable.getCause();
            throwable.setStackTrace(throwable.getStackTrace());
            if(isGeneric){
                qrpcResponse.setResponse(JSON.toJSON(throwable));
            }
            else{
                qrpcResponse.setResponse(throwable);
            }
            qrpcResponse.setErrorType(throwable.getClass().getSimpleName());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("provider exception, serviceName:").append(serviceName).append(" methodName:")
                    .append(methodName).append(" host:").append(remoteHost).append(" params:").append(Arrays.toString(qrpcRequest.getMethodArgs()));
            System.out.println(stringBuilder);
        }
        return qrpcResponse;
    }

    private boolean tryAcquire(){
        return rateLimiter.tryAcquire();
    }

    private String getAppNameOfClient(QRPCRequest request){
        String appName = (String)request.getFeature(CLIENT_NAME);
        if(appName == null){
            return UNKOWN;
        }
        return appName;
    }
}
