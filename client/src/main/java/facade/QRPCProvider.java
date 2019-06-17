package facade;

import model.MetaData;
import constants.SerializeTypeEnum;
import container.ServiceSingletonContainer;
import exception.QRPCException;
import exception.ValidateException;
import metadata.service.MetaDataService;
import org.apache.commons.lang.StringUtils;
import process.ProcessService;
import process.impl.ProcessServiceImpl;
import utils.ClassUtils;

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

    // 主流程处理服务
    private ProcessService processService = ServiceSingletonContainer.getInstance(ProcessServiceImpl.class);

    public QRPCProvider() {
        // 设置初始值
        metaData.setVersion("1.0.0");
        metaData.setExecuteTimeout(3000);
        metaData.setIdleTimeout(20000L);
        metaData.setSerializeType(SerializeTypeEnum.KRYO.getType());
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

    public void setInterfaceName(String interfaceName){
        metaData.setInterfaceName(interfaceName);
        try {
            metaData.setInterfaceClazz(ClassUtils.name2class(interfaceName));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("请检查接口名是否正确:" + interfaceName);
        }
        metaData.setUniqueName(interfaceName + this.metaData.getVersion());
    }

    public void setTarget(Object target){
        metaData.setTarget(target);
    }

    public void setVersion(String version){
        metaData.setVersion(version);
    }

    private void validate() throws ValidateException {
        if(StringUtils.isEmpty(metaData.getInterfaceName())){
            throw new ValidateException("接口名称为空！");
        }
    }

    private void publish(MetaData metaData) throws QRPCException {
        // 避免重复publish
        if(!isPublished.compareAndSet(false, true)){
            return;
        }
        processService.publish(metaData);

    }
}
