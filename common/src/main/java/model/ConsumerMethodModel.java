package model;


import java.lang.reflect.Method;
import java.util.Arrays;

public class ConsumerMethodModel {

    public static final String GENERIC_INVOKE = "$invoke";

    private final Method method;

    private final String enterMsg;

    private final MetaData metaData;

    private final String[] parameterTypes;

    private final Class<?>[] parameterClasses;

    private final Class<?> returnClass;

    private final String methodName;

    private final boolean isGeneric;

    private final int timeout;

    public ConsumerMethodModel(Method method, MetaData metaData) {
        this.method = method;
        this.parameterClasses = method.getParameterTypes();
        this.returnClass = method.getReturnType();
        this.parameterTypes = this.getParameterTypes(parameterClasses);
        this.methodName = method.getName();
        this.metaData = metaData;
        this.isGeneric = methodName.equals(GENERIC_INVOKE) && parameterTypes != null && parameterTypes.length == 3;
        this.enterMsg = "invoke service:" + this.metaData.getUniqueName() + this.methodName + Arrays.toString(this.parameterTypes);
        this.timeout = metaData.getExecuteTimeout();
    }

    private String[] getParameterTypes(Class<?>[] args){
        if(args == null || args.length == 0){
            return new String[]{};
        }
        String[] parameterTypes = new String[args.length];
        for(int i = 0; i < args.length; i++){
            parameterTypes[i] = args[i].getName();
        }
        return parameterTypes;
    }

    private boolean isGeneric(){
        return isGeneric;
    }

    public static String getGenericInvoke() {
        return GENERIC_INVOKE;
    }

    public Method getMethod() {
        return method;
    }

    public String getEnterMsg() {
        return enterMsg;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public Class<?>[] getParameterClasses() {
        return parameterClasses;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getUniqueName(){
        return metaData.getUniqueName();
    }
}
