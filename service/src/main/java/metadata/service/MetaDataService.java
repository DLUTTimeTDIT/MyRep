package metadata.service;

import model.MetaData;

/**
 * 元数据服务接口
 */
public interface MetaDataService {

    /**
     * 发布metaData
     * @param metaData
     */
    void publish(MetaData metaData);

    /**
     * 注销metaData
     * @param metaData
     */
    void unregister(MetaData metaData);

    /**
     * 订阅metaData
     * @param metaData
     */
    void subscribe(MetaData metaData);
}
