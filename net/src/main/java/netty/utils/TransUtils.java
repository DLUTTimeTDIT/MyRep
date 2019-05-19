package netty.utils;

import model.QRPCRequest;
import netty.request.BaseRequest;
import netty.request.RPCRequest;
import netty.serialize.Encoder;
import utils.ClassUtils;

import java.util.Map;

/**
 * 对象转换工具
 */
public class TransUtils {

    public static QRPCRequest convert(RPCRequest rpcRequest) throws Exception{
        QRPCRequest qrpcRequest = new QRPCRequest();
        qrpcRequest.setSerializeType(rpcRequest.getCodecType());
        String[] argTypes = rpcRequest.getArgTypes();
        qrpcRequest.setMethodParamTypes(argTypes);
        qrpcRequest.setMethodName(rpcRequest.getMethodName());
        qrpcRequest.setServiceName(rpcRequest.getTargetInstanceName());

        byte serializeType = rpcRequest.getCodecType();
        byte[][] argsBytes = rpcRequest.getRequestObjects();

        Object[] requestObjects = new Object[argsBytes.length];
        for(int i = 0; i < argsBytes.length; i++){
            requestObjects[i] = CodecTypeUtils.getDecoder(serializeType).decode(argsBytes[i], ClassUtils.name2class(argTypes[i]));
        }
        byte[] requestProps = rpcRequest.getRequestProps();
        qrpcRequest.setMethodArgs(requestObjects);
        Map<String, Object> properties = (Map<String, Object>) CodecTypeUtils.getDecoder(serializeType).decode(requestProps, Map.class);
        qrpcRequest.setFeatures(properties);
        return qrpcRequest;
    }

    public static BaseRequest convert(QRPCRequest qrpcRequest, byte codecType, int timeout) throws Exception{
        String targetInstanceName = qrpcRequest.getServiceName();
        String methodName = qrpcRequest.getMethodName();
        String[] argTypes = qrpcRequest.getMethodParamTypes();
        Object[] args = qrpcRequest.getMethodArgs();
        Map<String, Object> features = qrpcRequest.getFeatures();
        byte[][] argBytes;
        byte[] featuresBytes;
        int requestSize = 0;
        argBytes = new byte[argTypes.length][];
        Encoder encoder = CodecTypeUtils.getEncoder(codecType);
        int i = 0;
        for(Object o : args){
            argBytes[i] = encoder.encode(o);
            requestSize += argBytes[i++].length;
        }
        featuresBytes = encoder.encode(features);
        return new RPCRequest(timeout, targetInstanceName, methodName, argTypes, argBytes,
                featuresBytes, codecType, requestSize);
    }
}
