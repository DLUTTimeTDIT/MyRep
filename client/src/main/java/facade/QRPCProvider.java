package facade;

import common.MetaData;
import constants.ProxyTypeEnum;
import constants.SerializeTypeEnum;
import exception.ValidateException;
import metadata.service.MetaDataService;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务提供方
 */
public class QRPCProvider {

    @Resource
    private MetaDataService metaDataService;

    // 是否进行了初始化
    private final AtomicBoolean inited = new AtomicBoolean(false);

    // 是否发布了
    private final AtomicBoolean isPublished = new AtomicBoolean(false);

    // 待提供服务对应的元数据
    private MetaData metaData = new MetaData();

    public QRPCProvider() {
        // 设置初始值
        metaData.setVersion("1.0.0");
        metaData.setExecuteTimeout(3000L);
        metaData.setIdleTimeout(20000L);
        metaData.setProxyType(ProxyTypeEnum.JDK);
        metaData.setSerializeType(SerializeTypeEnum.PROTOTUF);
    }

    /**
     * 对外发布服务
     */
    public void init() throws Exception {
        // 避免被初始化多次
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        this.validate();
        this.publish(metaData);
    }

    private void validate() throws ValidateException {
        if(StringUtils.isEmpty(metaData.getInterfaceName())){
            throw new ValidateException("接口名称为空！");
        }
        // todo 各种校验
    }

    private void publish(MetaData metaData){
        // 避免重复publish
        if(!isPublished.compareAndSet(false, true)){
            return;
        }
        metaDataService.publish(metaData);
    }
}
