package model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ConsumerServiceModel {

    private final MetaData metaData;

    private final Object proxy;

    private final Map<Method, ConsumerMethodModel> methodModels = new IdentityHashMap<>();

    public ConsumerServiceModel(MetaData metaData, Object proxy) {
        this.metaData = metaData;
        this.proxy = proxy;

        if(proxy != null){
            Class<?> proxyClass = proxy.getClass();
            Field[] fields = proxyClass.getDeclaredFields();
            try {
            for(Field field : fields){
                field.setAccessible(true);
                    Method method = (Method) field.get(this.proxy);
                    methodModels.put(method, new ConsumerMethodModel(method, metaData));
            }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public Object getProxy() {
        return proxy;
    }

    public ConsumerMethodModel getMethodModel(Method method) {
        return methodModels.get(method);
    }
}
