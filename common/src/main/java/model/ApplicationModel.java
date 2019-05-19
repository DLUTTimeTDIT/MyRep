package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 维护一些映射关系和对象
 */
public class ApplicationModel {

    /**
     * 服务名与服务对象的映射
     */
    private static final ConcurrentMap<String, ProviderServiceModel> providerServiceModelConcurrentMap = new ConcurrentHashMap<>();

    /**
     * 服务名与服务消费对象模型的映射
     */
    private static final ConcurrentMap<String, ConsumerServiceModel> consumerServiceModelConcurrentMap = new ConcurrentHashMap<>();

    public static boolean addProviderService(String serviceName, ProviderServiceModel providerServiceModel){
        if(providerServiceModelConcurrentMap.putIfAbsent(serviceName, providerServiceModel) == null){
            return false;
        }
        return true;
    }

    public static boolean addConsumerService(String serviceName, ConsumerServiceModel consumerServiceModel){
        if(consumerServiceModelConcurrentMap.putIfAbsent(serviceName, consumerServiceModel) == null){
            return false;
        }
        return true;
    }

    public static ConsumerServiceModel getConsumerServiceModel(String serviceName){
        return consumerServiceModelConcurrentMap.get(serviceName);
    }

    public static ProviderServiceModel getProviderServiceModel(String serviceName){
        return providerServiceModelConcurrentMap.get(serviceName);
    }

    public static List<ConsumerServiceModel> allConsumerServices(){
        return new ArrayList<>(consumerServiceModelConcurrentMap.values());
    }

    public static List<ProviderServiceModel> allProviderServices(){
        return new ArrayList<>(providerServiceModelConcurrentMap.values());
    }

    public void initProviderService(String serviceName, ProviderServiceModel providerServiceModel){
        addProviderService(serviceName, providerServiceModel);
    }
}
