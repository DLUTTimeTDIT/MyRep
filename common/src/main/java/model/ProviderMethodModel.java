package model;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 方法模型
 */
public class ProviderMethodModel {
    private final Method method;

    private final String methodName;

    private final String[] methodArgTypes;

    private final String serviceName;

    private final int timeout;

    public ProviderMethodModel(Method method, String serviceName, int timeout) {
        this.method = method;
        this.serviceName = serviceName;
        this.timeout = timeout;
        this.methodName = method.getName();

        Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length > 0){
            this.methodArgTypes = new String[parameterTypes.length];
            int index = 0;
            for(Class<?> paramType : parameterTypes){
                methodArgTypes[index++] = paramType.getName();
            }
        }else{
            methodArgTypes = new String[0];
        }
    }

    public Method getMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getMethodArgTypes() {
        return methodArgTypes;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderMethodModel that = (ProviderMethodModel) o;
        return timeout == that.timeout &&
                method.equals(that.method) &&
                methodName.equals(that.methodName) &&
                Arrays.equals(methodArgTypes, that.methodArgTypes) &&
                serviceName.equals(that.serviceName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method, methodName, serviceName, timeout);
        result = 31 * result + Arrays.hashCode(methodArgTypes);
        return result;
    }
}
