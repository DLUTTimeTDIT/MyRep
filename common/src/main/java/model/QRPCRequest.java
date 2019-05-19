package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * QRPC请求对象
 */
public class QRPCRequest implements Serializable {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法参数类型列表
     */
    private String[] methodParamTypes;

    /**
     * 方法参数列表
     */
    private transient Object[] methodArgs;

    /**
     * 消费者IP
     */
    private String consumerIP;

    /**
     * 存放调用上下文序列之后的结果
     */
    private byte[] invokeContext;

    /**
     * 是否需要回调
     */
    private boolean needCallBack;

    /**
     * 扩展字段
     */
    private Map<String, Object> features;

    /**
     * 序列化类型
     */
    private transient byte serializeType;

    /**
     * 参数类类型
     */
    private transient Class<?>[] parameterClasses;

    /**
     * 返回类类型
     */
    private transient Class<?> returnClass;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getMethodParamTypes() {
        return methodParamTypes;
    }

    public void setMethodParamTypes(String[] methodParamTypes) {
        this.methodParamTypes = methodParamTypes;
    }

    public Object[] getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(Object[] methodArgs) {
        this.methodArgs = methodArgs;
    }

    public String getConsumerIP() {
        return consumerIP;
    }

    public void setConsumerIP(String consumerIP) {
        this.consumerIP = consumerIP;
    }

    public byte[] getInvokeContext() {
        return invokeContext;
    }

    public void setInvokeContext(byte[] invokeContext) {
        this.invokeContext = invokeContext;
    }

    public boolean isNeedCallBack() {
        return needCallBack;
    }

    public void setNeedCallBack(boolean needCallBack) {
        this.needCallBack = needCallBack;
    }

    public void addFeature(String key, Object value){
        if(features == null){
            features = new HashMap<>();
        }
        features.put(key, value);
    }

    public Map<String, Object> getFeatures(){
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    public Object getFeature(String key){
        return features.get(key);
    }

    public byte getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(byte serializeType) {
        this.serializeType = serializeType;
    }

    public Class<?>[] getParameterClasses() {
        return parameterClasses;
    }

    public void setParameterClasses(Class<?>[] parameterClasses) {
        this.parameterClasses = parameterClasses;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(Class<?> returnClass) {
        this.returnClass = returnClass;
    }

    /**
     * 方法key
     * @return
     */
    public String getMethodKey(){
        StringBuilder stringBuilder = new StringBuilder(serviceName);
        stringBuilder.append(methodName);
        for(int i = 0; i < methodParamTypes.length; i++){
            stringBuilder.append(methodParamTypes[i]);
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "QRPCRequest{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodParamTypes=" + Arrays.toString(methodParamTypes) +
                ", methodArgs=" + Arrays.toString(methodArgs) +
                ", consumerIP='" + consumerIP + '\'' +
                ", invokeContext=" + Arrays.toString(invokeContext) +
                ", needCallBack=" + needCallBack +
                ", features=" + features +
                ", serializeType=" + serializeType +
                ", parameterClasses=" + Arrays.toString(parameterClasses) +
                ", returnClass=" + returnClass +
                '}';
    }
}
