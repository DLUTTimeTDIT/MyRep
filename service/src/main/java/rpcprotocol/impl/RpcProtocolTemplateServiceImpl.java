package rpcprotocol.impl;

import address.AddressService;
import address.impl.AddressServiceImpl;
import config.ConfigServer;
import container.ServiceSingletonContainer;
import context.InvokerContext;
import exception.QRPCTimeoutException;
import exception.QRPCException;
import model.ConsumerMethodModel;
import model.MetaData;
import model.QRPCRequest;
import netty.client.NettyClientFactory;
import netty.client.RemotingURL;
import route.RouteTypeEnum;
import route.impl.ConsistentRouteServiceImpl;
import rpcprotocol.RpcProtocolService;
import rpcprotocol.RpcProtocolTemplateService;
import utils.ThreadLocalUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RpcProtocolTemplateServiceImpl implements RpcProtocolTemplateService {

    private final AddressService addressService = ServiceSingletonContainer.getInstance(AddressServiceImpl.class);

    private final RpcProtocolService rpcProtocolService = ServiceSingletonContainer.getInstance(RpcProtocolServiceImpl.class);

    private final NettyClientFactory clientFactory = NettyClientFactory.getInstance();

    public static final String TARGET_SERVER_IP = "tsi";

    private static final int ROUTE_TRY_TIMES = 10;

    private static final int EXECUTE_TRY_TIMES = 3;

    @Override
    public Object invokeWithMethodObject(ConsumerMethodModel consumerMethodModel, Object[] args) throws QRPCException, Throwable {
        final long beginTime = System.currentTimeMillis();
        final String serviceName = consumerMethodModel.getMethodName();
        args = (null == args) ? new Object[0] : args;

        InvokerContext invokerContext = new InvokerContext();
        try{
            return doInvoke(consumerMethodModel, args, invokerContext);
        } catch (Throwable t){
            String errorMsg = null;
            String methodName = consumerMethodModel.getMethodName();
            if(t.getCause() instanceof QRPCTimeoutException){
                errorMsg = "service:" + serviceName + " methodName:" + methodName + "invoke timeout:" + (System.currentTimeMillis() - beginTime);
            } else{
                t.printStackTrace();
                errorMsg = "service:" + serviceName + " methodName:" + methodName + "invoke error:" + t.getCause().getMessage();
            }
            System.out.println(errorMsg);
            t.printStackTrace();
            throw t;
        } finally {
            // help GC
            invokerContext = null;
        }
    }

    @Override
    public void registerProvider(MetaData metaData) throws QRPCException {
        rpcProtocolService.registerProvider(metaData);
    }

    @Override
    public void shutdownQRPCServer() throws QRPCException {
        try{
            rpcProtocolService.shutdownGracefully();
        } catch (Exception e) {
            throw new QRPCException("showdown server failed");
        }
    }

    private Object doInvoke(ConsumerMethodModel consumerMethodModel, Object[] args, InvokerContext invokerContext) throws Throwable{

        final String serviceName = consumerMethodModel.getUniqueName();
        final String methodName = consumerMethodModel.getMethodName();
        String[] parameterTypes = consumerMethodModel.getParameterTypes();

        // 组装qrpcRequest
        final QRPCRequest qrpcRequest = new QRPCRequest();
        qrpcRequest.setServiceName(serviceName);
        qrpcRequest.setMethodName(methodName);
        qrpcRequest.setMethodParamTypes(parameterTypes);
        qrpcRequest.setMethodArgs(args);
        qrpcRequest.setReturnClass(consumerMethodModel.getReturnClass());
        qrpcRequest.setParameterClasses(consumerMethodModel.getParameterClasses());

        MetaData metaData = consumerMethodModel.getMetaData();

        Object responseObj = null;
        List<RemotingURL> remotingURLList = null;
        RemotingURL remotingURL = null;
        int tryTimes = 0;
        // 寻找可用服务地址
        while(true){
            tryTimes ++;
            if(metaData.isBroadcast()){
                remotingURLList = selectAndValidateAddressList(metaData, consumerMethodModel, args);
            } else{
                try {
                    String consistent = metaData.getConsistent();
                    if (null != consistent) {
                        ThreadLocalUtils.set(ConsistentRouteServiceImpl.CONSISTENT_KEY, consistent);
                    }
                    remotingURL = selectAndValidateAddress(metaData, consumerMethodModel, args, metaData.getConnectionIndex());
                } finally {
                    ThreadLocalUtils.remove(ConsistentRouteServiceImpl.CONSISTENT_KEY);
                }
            }

            if(remotingURL == null && (remotingURLList == null || remotingURLList.isEmpty())){
                throw new QRPCException("server route failed, serviceName" + serviceName);
            }

            if(remotingURL != null){
                invokerContext.setRemotingURL(remotingURL);
                String remoteIp = remotingURL.getHost();
                ThreadLocalUtils.set(TARGET_SERVER_IP, remoteIp);

                try{
                    responseObj = doInvoke(qrpcRequest, consumerMethodModel, remotingURL);
                } catch (Throwable throwable){
                    if(tryTimes < EXECUTE_TRY_TIMES){
                        continue;
                    } else{
                        throw throwable;
                    }
                }
            } else{
                for (RemotingURL eachRemotingURL : remotingURLList) {
                    try {
                        responseObj = doInvoke(qrpcRequest, consumerMethodModel, eachRemotingURL);
                    } catch (Throwable throwable) {
                        if (tryTimes < EXECUTE_TRY_TIMES) {
                            continue;
                        } else {
                            throw throwable;
                        }
                    }
                }
            }
            return responseObj;
        }
    }

    private Object doInvoke(QRPCRequest qrpcRequest, ConsumerMethodModel consumerMethodModel, RemotingURL remotingURL) throws Throwable {
        Object responseObj = rpcProtocolService.invoke(qrpcRequest, consumerMethodModel, remotingURL);

        if(responseObj instanceof Throwable){
            throw (Throwable) responseObj;
        }
        return responseObj;
    }

    // 这个地方参数先多传点 现在没用到这些参数只是因为路由策略还做的很简单 之后加上一些其他的路由策略 可能会用到这些参数
    private List<RemotingURL> selectAndValidateAddressList(MetaData metaData, ConsumerMethodModel consumerMethodModel, Object[] args){
        List<RemotingURL> remotingURLList = new ArrayList<>();
        for(int i = 0; i < ROUTE_TRY_TIMES; i++){
            List<String> urls = addressService.getServiceAddresses(metaData.getUniqueName());
            if(urls == null || urls.isEmpty()){
                continue;
            }
            urls.forEach(url->{
                RemotingURL remotingURL = this.validateTarget(url, metaData.getSerializeType());
                if(remotingURL == null){
                    if(url.length() > 0){
                        System.out.println("RpcProtocolTemplateServiceImpl#selectAndValidateAddress invalid address");
                        addressService.invalidateAddress(metaData.getUniqueName(), url);
                    }
                }
            });
            break;
        }
        return remotingURLList;
    }

    private RemotingURL selectAndValidateAddress(MetaData metaData, ConsumerMethodModel consumerMethodModel, Object[] args, int connectionIndex){
        RouteTypeEnum routeTypeEnum = RouteTypeEnum.RANDOM;
        if(metaData.getRouteType() != null && metaData.getRouteType() == 1){
            routeTypeEnum = RouteTypeEnum.CONSISTENT_HASH;
        }
        for(int i = 0; i < ROUTE_TRY_TIMES; i++){
            String targetUrl = addressService.getServiceAddress(metaData.getUniqueName(), consumerMethodModel.getMethodName(),
                    consumerMethodModel.getParameterTypes(), args, routeTypeEnum);
            if(targetUrl == null){
                continue;
            }
            RemotingURL remotingURL = this.validateTarget(targetUrl, metaData.getSerializeType());
            if(remotingURL == null){
                if(targetUrl.length() > 0){
                    System.out.println("RpcProtocolTemplateServiceImpl#selectAndValidateAddress invalid address" + targetUrl);
                    addressService.invalidateAddress(metaData.getUniqueName(), targetUrl);
                }
            } else{
                return remotingURL;
            }
        }
        return null;
    }

    private RemotingURL validateTarget(String url, byte serializeType){
//        RemotingURL remotingURL = RemotingURL.valueOf(url, connectionIndex);
        Map<String, String> params = new HashMap<>();
        params.put(RemotingURL.SERIALIZE_TYPE, String.valueOf(serializeType));
        RemotingURL remotingURL = new RemotingURL(url, ConfigServer.SERVER_PORT, params);
        try {
            return clientFactory.get(remotingURL) != null ? remotingURL : null;
        } catch (ExecutionException e) {
            System.out.println("RpcProtocolTemplateServiceImpl#validateTarget error");
            return null;
        }
    }
}
