package model;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 服务数据模型 用来做服务维度的缓存
 */
public class ProviderServiceModel {
    /**
     * 服务名称
     */
    private final String serviceName;

    /**
     * 服务对应实例
     */
    private final Object serviceInstance;

    /**
     * 元数据
     */
    private final MetaData metaData;

    private final Map<String, List<ProviderMethodModel>> methods = new HashMap<>();

    public ProviderServiceModel(String serviceName, MetaData metaData, Object serviceInstance) {
        this.serviceName = serviceName;
        this.serviceInstance = serviceInstance;
        this.metaData = metaData;

        initMethod();
    }

    private void initMethod(){
        Method[] methods = metaData.getInterfaceClazz().getMethods();

        for(Method method : methods){
            method.setAccessible(true);

            List<ProviderMethodModel> methodModels = this.methods.putIfAbsent(method.getName(), new ArrayList<ProviderMethodModel>(){{
                add(new ProviderMethodModel(method, serviceName, metaData.getExecuteTimeout()));
            }});
        }
    }

    public ProviderMethodModel getMethodModel(String methodName, String[] argTypes){
        List<ProviderMethodModel> methodModels = methods.get(methodName);
        if(methodModels == null){
            System.out.println("服务端无该方法"+methodName);
            return null;
        }
        methodModels.forEach(providerMethodModel -> {
            if(Arrays.equals(argTypes, providerMethodModel.getMethodArgTypes()));
        });
        return null;
    }

    public String getServiceName() {
        return serviceName;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public Map<String, List<ProviderMethodModel>> getMethods() {
        return methods;
    }

    public Object getServiceInstance() {
        return serviceInstance;
    }
}
